package com.casino.pokervue.ui.table

import android.app.Application
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

    private var calculationJob: Job? = null

    init {
        viewModelScope.launch {
            opponents = settingsRepository.defaultOpponents.first()
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

        if (knownPlayerCards.size == 2) {
            val allCards = knownPlayerCards + knownBoardCards
            if (allCards.size >= 5) {
                val best = HandEvaluator.evaluateBestHand(allCards)
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
            }
        } else {
            currentHandName = null
            highlightedCards = emptySet()
        }

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
                    opponentCount = currentOpponents
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
}