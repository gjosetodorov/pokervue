package com.casino.pokervue

import com.casino.pokervue.logic.EquityCalculator
import com.casino.pokervue.logic.HandEvaluator
import com.casino.pokervue.model.Card
import com.casino.pokervue.model.Rank
import com.casino.pokervue.model.Suit
import org.junit.Assert.assertTrue
import org.junit.Test

class EquityCalculatorTest {

    @Test
    fun pocketAcesHasHighEquityHeadsUp() {
        val result = EquityCalculator.calculate(
            holeCards = listOf(Card(Rank.ACE, Suit.SPADES), Card(Rank.ACE, Suit.HEARTS)),
            boardCards = emptyList(),
            opponentCount = 1,
            iterations = 20_000
        )
        // Pocket aces preflop heads-up should win roughly 85% of the time — allow a wide-ish margin
        // since this is a randomized simulation, not a deterministic exact calculation
        assertTrue("winPercent was ${result.winPercent}", result.winPercent in 78.0..92.0)
    }

    @Test
    fun percentagesSumToRoughly100() {
        val result = EquityCalculator.calculate(
            holeCards = listOf(Card(Rank.KING, Suit.SPADES), Card(Rank.QUEEN, Suit.HEARTS)),
            boardCards = emptyList(),
            opponentCount = 2,
            iterations = 10_000
        )
        val sum = result.winPercent + result.tiePercent + result.losePercent
        assertTrue("sum was $sum", sum in 99.0..101.0)
    }

    @Test
    fun debugSingleHandEvaluation() {
        val aces = listOf(Card(Rank.ACE, Suit.SPADES), Card(Rank.ACE, Suit.HEARTS))
        val board = listOf(
            Card(Rank.TWO, Suit.CLUBS), Card(Rank.SEVEN, Suit.DIAMONDS), Card(Rank.NINE, Suit.SPADES),
            Card(Rank.JACK, Suit.HEARTS), Card(Rank.THREE, Suit.CLUBS)
        )
        val opponent = listOf(Card(Rank.KING, Suit.CLUBS), Card(Rank.QUEEN, Suit.DIAMONDS))

        val playerHand = HandEvaluator.evaluate(aces + board)
        val opponentHand = HandEvaluator.evaluate(opponent + board)

        println("Player: ${playerHand.category} ${playerHand.tiebreakers}")
        println("Opponent: ${opponentHand.category} ${opponentHand.tiebreakers}")
        println("Player wins: ${playerHand > opponentHand}")

        assertTrue(playerHand > opponentHand) // pocket aces vs KQ on this board should clearly win
    }

    @Test
    fun debugFullCalculation() {
        val aces = listOf(Card(Rank.ACE, Suit.SPADES), Card(Rank.ACE, Suit.HEARTS))
        val result = EquityCalculator.calculate(
            holeCards = aces,
            boardCards = emptyList(),
            opponentCount = 1,
            iterations = 1000
        )
        println("Win: ${result.winPercent}, Tie: ${result.tiePercent}, Lose: ${result.losePercent}")
    }
}