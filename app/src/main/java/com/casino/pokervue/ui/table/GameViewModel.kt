package com.casino.pokervue.ui.table

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.casino.pokervue.data.SettingsRepository
import com.casino.pokervue.logic.EquityCalculator
import com.casino.pokervue.logic.HandEvaluator
import com.casino.pokervue.logic.OutsCalculator
import com.casino.pokervue.logic.PartialHandDetector
import com.casino.pokervue.model.Card
import com.casino.pokervue.model.HandCategory
import com.casino.pokervue.model.displayName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface EquityState {
    data object Idle : EquityState
    data object Calculating : EquityState
    data class Result(
        val winPercent: Double,
        val tiePercent: Double,
        val losePercent: Double,
        val handCategoryPercents: Map<HandCategory, Double>
    ) : EquityState
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepository = SettingsRepository(application.applicationContext)

    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        manager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        application.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    var playerCards by mutableStateOf(listOf<Card?>(null, null))
        private set
    var communityCards by mutableStateOf(listOf<Card?>(null, null, null, null, null))
        private set
    var opponents by mutableStateOf(1)
        private set
    var equityState by mutableStateOf<EquityState>(EquityState.Idle)
        private set
    var outs by mutableStateOf<List<Card>>(emptyList())
        private set
    var currentHandName by mutableStateOf<String?>(null)
        private set
    var highlightedCards by mutableStateOf<Set<Card>>(emptySet())
        private set
    var hapticsEnabledState by mutableStateOf(true)
        private set

    private var calculationJob: Job? = null
    private var lastKnownCategory: HandCategory? = null
    private var hapticsEnabled: Boolean = true
    private var simulationIterations: Int = 3000

    init {
        viewModelScope.launch {
            opponents = settingsRepository.defaultOpponents.first()
        }
        viewModelScope.launch {
            settingsRepository.hapticFeedback.collect { enabled ->
                hapticsEnabled = enabled
                hapticsEnabledState = enabled
            }
        }
        viewModelScope.launch {
            settingsRepository.simulationIterations.collect { iterations ->
                simulationIterations = iterations
            }
        }
    }

    fun setPlayerCard(index: Int, card: Card) {
        playerCards = playerCards.toMutableList().apply { this[index] = card }
        recalculate()
    }

    fun setCommunityCard(index: Int, card: Card) {
        communityCards = communityCards.toMutableList().apply { this[index] = card }
        recalculate()
    }

    fun updateOpponents(value: Int) {
        opponents = value
        recalculate()
    }

    fun reset() {
        playerCards = listOf(null, null)
        communityCards = listOf(null, null, null, null, null)
        equityState = EquityState.Idle
        outs = emptyList()
        currentHandName = null
        highlightedCards = emptySet()
        lastKnownCategory = null
        calculationJob?.cancel()
        viewModelScope.launch {
            opponents = settingsRepository.defaultOpponents.first()
        }
    }

    private fun recalculate() {
        val knownPlayerCards = playerCards.filterNotNull()
        val knownBoardCards = communityCards.filterNotNull()

        outs = if (knownPlayerCards.size == 2) {
            OutsCalculator.calculateOuts(knownPlayerCards, knownBoardCards)
        } else {
            emptyList()
        }

        var newCategory: HandCategory? = null

        if (knownPlayerCards.size == 2) {
            val allCards = knownPlayerCards + knownBoardCards
            if (allCards.size >= 5) {
                val best = HandEvaluator.evaluateBestHand(allCards)
                newCategory = best.rank.category
                if (best.rank.category == HandCategory.HIGH_CARD) {
                    currentHandName = null
                    highlightedCards = emptySet()
                } else {
                    currentHandName = best.rank.displayName()
                    highlightedCards = best.cards.toSet()
                }
            } else {
                val partial = PartialHandDetector.detect(allCards)
                currentHandName = partial?.name
                highlightedCards = partial?.cards?.toSet() ?: emptySet()
                newCategory = when (partial?.name) {
                    "Pair" -> HandCategory.PAIR
                    "Two Pair" -> HandCategory.TWO_PAIR
                    "Three of a Kind" -> HandCategory.THREE_OF_A_KIND
                    "Four of a Kind" -> HandCategory.FOUR_OF_A_KIND
                    else -> null
                }
            }
        } else {
            currentHandName = null
            highlightedCards = emptySet()
        }

        val effectiveLastCategory = lastKnownCategory ?: HandCategory.HIGH_CARD
        val effectiveNewCategory = newCategory ?: HandCategory.HIGH_CARD

        if (knownPlayerCards.size == 2 && effectiveNewCategory.strength > effectiveLastCategory.strength) {
            triggerHandImprovedHaptic()
        }
        lastKnownCategory = newCategory

        if (knownPlayerCards.size < 2) {
            equityState = EquityState.Idle
            return
        }

        val currentOpponents = opponents

        calculationJob?.cancel()
        calculationJob = viewModelScope.launch {
            equityState = EquityState.Calculating
            val result = withContext(Dispatchers.Default) {
                EquityCalculator.calculate(
                    holeCards = knownPlayerCards,
                    boardCards = knownBoardCards,
                    opponentCount = currentOpponents,
                    iterations = simulationIterations
                )
            }
            equityState = EquityState.Result(
                winPercent = result.winPercent,
                tiePercent = result.tiePercent,
                losePercent = result.losePercent,
                handCategoryPercents = result.handCategoryPercents
            )
        }
    }

    private fun triggerHandImprovedHaptic() {
        if (!hapticsEnabled) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(250, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(250)
        }
    }
}