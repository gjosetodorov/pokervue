package com.casino.pokervue.logic

import com.casino.pokervue.model.Card
import com.casino.pokervue.model.Deck
import com.casino.pokervue.model.HandCategory

data class EquityResult(
    val winPercent: Double,
    val tiePercent: Double,
    val losePercent: Double,
    val handCategoryPercents: Map<HandCategory, Double>
)

object EquityCalculator {
    fun calculate(
        holeCards: List<Card>,
        boardCards: List<Card>,
        opponentCount: Int,
        iterations: Int = 3_000
    ): EquityResult {
        require(holeCards.size == 2) { "Need exactly 2 hole cards to calculate equity" }
        require(opponentCount in 1..8) { "Opponent count must be between 1 and 8" }

        val knownBoard = boardCards.filterNotNull()
        val cardsNeededOnBoard = 5 - knownBoard.size

        var wins = 0
        var ties = 0
        var losses = 0
        val categoryCounts = mutableMapOf<HandCategory, Int>()

        repeat(iterations) {
            val excluded = (holeCards + knownBoard).toSet()
            val deck = Deck(excluding = excluded)

            val opponentHands = List(opponentCount) { deck.drawRandom(2) }
            val fullBoard = knownBoard + deck.drawRandom(cardsNeededOnBoard)

            val playerBest = HandEvaluator.evaluate(holeCards + fullBoard)
            val opponentBests = opponentHands.map { HandEvaluator.evaluate(it + fullBoard) }
            val bestOpponent = opponentBests.max()

            categoryCounts[playerBest.category] = (categoryCounts[playerBest.category] ?: 0) + 1

            when {
                playerBest > bestOpponent -> wins++
                playerBest == bestOpponent -> ties++
                else -> losses++
            }
        }

        val categoryPercents = HandCategory.entries.associateWith { category ->
            (categoryCounts[category] ?: 0) * 100.0 / iterations
        }

        return EquityResult(
            winPercent = wins * 100.0 / iterations,
            tiePercent = ties * 100.0 / iterations,
            losePercent = losses * 100.0 / iterations,
            handCategoryPercents = categoryPercents
        )
    }
}