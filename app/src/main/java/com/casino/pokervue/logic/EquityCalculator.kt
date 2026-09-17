package com.casino.pokervue.logic

import com.casino.pokervue.model.Card
import com.casino.pokervue.model.Deck

data class EquityResult(
    val winPercent: Double,
    val tiePercent: Double,
    val losePercent: Double
)

object EquityCalculator {
    fun calculate(
        holeCards: List<Card>,
        boardCards: List<Card>,
        opponentCount: Int,
        iterations: Int = 3_000
    ): EquityResult {
        require(holeCards.size == 2) { }
        require(opponentCount in 1..8) { }

        val knownBoard = boardCards.filterNotNull()
        val cardsNeededOnBoard = 5 - knownBoard.size

        var wins = 0
        var ties = 0
        var losses = 0

        repeat(iterations) {
            val excluded = (holeCards + knownBoard).toSet()
            val deck = Deck(excluding = excluded)

            val opponentHands = List(opponentCount) { deck.drawRandom(2) }
            val fullBoard = knownBoard + deck.drawRandom(cardsNeededOnBoard)

            val playerBest = HandEvaluator.evaluate(holeCards + fullBoard)
            val opponentBests = opponentHands.map { HandEvaluator.evaluate(it + fullBoard) }
            val bestOpponent = opponentBests.max()

            when {
                playerBest > bestOpponent -> wins++
                playerBest == bestOpponent -> ties++
                else -> losses++
            }
        }

        return EquityResult(
            winPercent = wins * 100.0 / iterations,
            tiePercent = ties * 100.0 / iterations,
            losePercent = losses * 100.0 / iterations
        )
    }
}