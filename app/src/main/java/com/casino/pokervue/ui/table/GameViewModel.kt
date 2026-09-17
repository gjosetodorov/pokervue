package com.casino.pokervue.ui.table

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.casino.pokervue.data.SettingsRepository
import com.casino.pokervue.model.Card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface EquityState {
    data object Idle : EquityState
    data object Calculating : EquityState
    data class Result(val winPercent: Double, val tiePercent: Double, val losePercent: Double) : EquityState
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
        calculationJob?.cancel()
        viewModelScope.launch {
            opponents = settingsRepository.defaultOpponents.first()
        }
    }

    private fun recalculate() {
        val knownPlayerCards = playerCards.filterNotNull()
        if (knownPlayerCards.size < 2) {
            equityState = EquityState.Idle
            return
        }

        calculationJob?.cancel()
        calculationJob = viewModelScope.launch {
            equityState = EquityState.Calculating
            val result = withContext(Dispatchers.Default) {
                EquityState.Result(winPercent = 0.0, tiePercent = 0.0, losePercent = 0.0)
            }
            equityState = result
        }
    }
}