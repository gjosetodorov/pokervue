package com.casino.pokervue

import com.casino.pokervue.logic.HandEvaluator
import com.casino.pokervue.model.Card
import com.casino.pokervue.model.HandCategory
import com.casino.pokervue.model.Rank
import com.casino.pokervue.model.Suit
import com.casino.pokervue.model.isRoyalFlush
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HandEvaluatorTest {

    private fun card(rank: Rank, suit: Suit) = Card(rank, suit)

    @Test
    fun royalFlushIsDetectedAndBeatsEverything() {
        val hand = listOf(
            card(Rank.TEN, Suit.SPADES), card(Rank.JACK, Suit.SPADES), card(Rank.QUEEN, Suit.SPADES),
            card(Rank.KING, Suit.SPADES), card(Rank.ACE, Suit.SPADES),
            card(Rank.TWO, Suit.HEARTS), card(Rank.THREE, Suit.CLUBS)
        )
        val result = HandEvaluator.evaluate(hand)
        assertEquals(HandCategory.STRAIGHT_FLUSH, result.category)
        assertEquals(14, result.tiebreakers[0])
        assertTrue(result.isRoyalFlush)
    }

    @Test
    fun straightFlushNotAceHighIsNotRoyal() {
        val hand = listOf(
            card(Rank.SIX, Suit.HEARTS), card(Rank.SEVEN, Suit.HEARTS), card(Rank.EIGHT, Suit.HEARTS),
            card(Rank.NINE, Suit.HEARTS), card(Rank.TEN, Suit.HEARTS),
            card(Rank.TWO, Suit.CLUBS), card(Rank.THREE, Suit.CLUBS)
        )
        val result = HandEvaluator.evaluate(hand)
        assertEquals(HandCategory.STRAIGHT_FLUSH, result.category)
        assertEquals(10, result.tiebreakers[0])
        assertTrue(!result.isRoyalFlush)
    }

    @Test
    fun fourOfAKind() {
        val hand = listOf(
            card(Rank.NINE, Suit.SPADES), card(Rank.NINE, Suit.HEARTS), card(Rank.NINE, Suit.DIAMONDS),
            card(Rank.NINE, Suit.CLUBS), card(Rank.KING, Suit.SPADES),
            card(Rank.TWO, Suit.HEARTS), card(Rank.THREE, Suit.CLUBS)
        )
        val result = HandEvaluator.evaluate(hand)
        assertEquals(HandCategory.FOUR_OF_A_KIND, result.category)
        assertEquals(9, result.tiebreakers[0]) // quad rank
        assertEquals(13, result.tiebreakers[1]) // kicker = King
    }

    @Test
    fun fullHousePicksBestTripsAndBestPair() {
        // 7 cards containing trip-Kings, trip-Queens, and a pair — should pick K-K-K + Q-Q as the full house
        val hand = listOf(
            card(Rank.KING, Suit.SPADES), card(Rank.KING, Suit.HEARTS), card(Rank.KING, Suit.DIAMONDS),
            card(Rank.QUEEN, Suit.SPADES), card(Rank.QUEEN, Suit.HEARTS), card(Rank.QUEEN, Suit.DIAMONDS),
            card(Rank.TWO, Suit.CLUBS)
        )
        val result = HandEvaluator.evaluate(hand)
        assertEquals(HandCategory.FULL_HOUSE, result.category)
        assertEquals(13, result.tiebreakers[0]) // trips = Kings
        assertEquals(12, result.tiebreakers[1]) // pair = Queens (the better available pair, not the leftover trip)
    }

    @Test
    fun flushRanksByHighCardsInOrder() {
        val hand = listOf(
            card(Rank.TWO, Suit.CLUBS), card(Rank.FIVE, Suit.CLUBS), card(Rank.NINE, Suit.CLUBS),
            card(Rank.JACK, Suit.CLUBS), card(Rank.KING, Suit.CLUBS),
            card(Rank.ACE, Suit.HEARTS), card(Rank.THREE, Suit.DIAMONDS)
        )
        val result = HandEvaluator.evaluate(hand)
        assertEquals(HandCategory.FLUSH, result.category)
        assertEquals(listOf(13, 11, 9, 5, 2), result.tiebreakers)
    }

    @Test
    fun normalStraight() {
        val hand = listOf(
            card(Rank.FOUR, Suit.SPADES), card(Rank.FIVE, Suit.HEARTS), card(Rank.SIX, Suit.DIAMONDS),
            card(Rank.SEVEN, Suit.CLUBS), card(Rank.EIGHT, Suit.SPADES),
            card(Rank.TWO, Suit.HEARTS), card(Rank.KING, Suit.CLUBS)
        )
        val result = HandEvaluator.evaluate(hand)
        assertEquals(HandCategory.STRAIGHT, result.category)
        assertEquals(8, result.tiebreakers[0])
    }

    @Test
    fun wheelStraightAceLow() {
        // A-2-3-4-5, Ace playing low — high card should be treated as 5, not 14
        val hand = listOf(
            card(Rank.ACE, Suit.SPADES), card(Rank.TWO, Suit.HEARTS), card(Rank.THREE, Suit.DIAMONDS),
            card(Rank.FOUR, Suit.CLUBS), card(Rank.FIVE, Suit.SPADES),
            card(Rank.NINE, Suit.HEARTS), card(Rank.KING, Suit.CLUBS)
        )
        val result = HandEvaluator.evaluate(hand)
        assertEquals(HandCategory.STRAIGHT, result.category)
        assertEquals(5, result.tiebreakers[0])
    }

    @Test
    fun wheelStraightLosesToSixHighStraight() {
        val wheel = HandEvaluator.evaluate(listOf(
            card(Rank.ACE, Suit.SPADES), card(Rank.TWO, Suit.HEARTS), card(Rank.THREE, Suit.DIAMONDS),
            card(Rank.FOUR, Suit.CLUBS), card(Rank.FIVE, Suit.SPADES),
            card(Rank.NINE, Suit.HEARTS), card(Rank.KING, Suit.CLUBS)
        ))
        val sixHigh = HandEvaluator.evaluate(listOf(
            card(Rank.TWO, Suit.SPADES), card(Rank.THREE, Suit.HEARTS), card(Rank.FOUR, Suit.DIAMONDS),
            card(Rank.FIVE, Suit.CLUBS), card(Rank.SIX, Suit.SPADES),
            card(Rank.NINE, Suit.HEARTS), card(Rank.KING, Suit.CLUBS)
        ))
        assertTrue(wheel < sixHigh)
    }

    @Test
    fun threeOfAKindWithKickers() {
        val hand = listOf(
            card(Rank.SEVEN, Suit.SPADES), card(Rank.SEVEN, Suit.HEARTS), card(Rank.SEVEN, Suit.DIAMONDS),
            card(Rank.KING, Suit.CLUBS), card(Rank.TWO, Suit.SPADES),
            card(Rank.NINE, Suit.HEARTS), card(Rank.FOUR, Suit.CLUBS)
        )
        val result = HandEvaluator.evaluate(hand)
        assertEquals(HandCategory.THREE_OF_A_KIND, result.category)
        assertEquals(listOf(7, 13, 9), result.tiebreakers) // trips, then 2 best kickers (K, 9)
    }

    @Test
    fun twoPairPicksTwoBestPairsAndBestKicker() {
        // Three pairs available (K,K / 8,8 / 3,3) — should use the top two pairs, K and 8, with best remaining kicker
        val hand = listOf(
            card(Rank.KING, Suit.SPADES), card(Rank.KING, Suit.HEARTS),
            card(Rank.EIGHT, Suit.SPADES), card(Rank.EIGHT, Suit.HEARTS),
            card(Rank.THREE, Suit.SPADES), card(Rank.THREE, Suit.HEARTS),
            card(Rank.QUEEN, Suit.CLUBS)
        )
        val result = HandEvaluator.evaluate(hand)
        assertEquals(HandCategory.TWO_PAIR, result.category)
        assertEquals(listOf(13, 8, 12), result.tiebreakers) // K pair, 8 pair, Q kicker (not the pair of 3s)
    }

    @Test
    fun onePairWithKickers() {
        val hand = listOf(
            card(Rank.FIVE, Suit.SPADES), card(Rank.FIVE, Suit.HEARTS),
            card(Rank.ACE, Suit.CLUBS), card(Rank.KING, Suit.DIAMONDS), card(Rank.NINE, Suit.SPADES),
            card(Rank.TWO, Suit.HEARTS), card(Rank.THREE, Suit.CLUBS)
        )
        val result = HandEvaluator.evaluate(hand)
        assertEquals(HandCategory.PAIR, result.category)
        assertEquals(listOf(5, 14, 13, 9), result.tiebreakers) // pair, then 3 best kickers
    }

    @Test
    fun highCardWhenNothingElseApplies() {
        val hand = listOf(
            card(Rank.TWO, Suit.SPADES), card(Rank.FIVE, Suit.HEARTS), card(Rank.NINE, Suit.DIAMONDS),
            card(Rank.JACK, Suit.CLUBS), card(Rank.KING, Suit.SPADES),
            card(Rank.THREE, Suit.HEARTS), card(Rank.SEVEN, Suit.CLUBS)
        )
        val result = HandEvaluator.evaluate(hand)
        assertEquals(HandCategory.HIGH_CARD, result.category)
        // best 5 of the 7 available: K, J, 9, 7, 5
        assertEquals(listOf(13, 11, 9, 7, 5), result.tiebreakers)
    }

    @Test
    fun handCategoriesCompareCorrectlyAcrossTypes() {
        val pair = HandEvaluator.evaluate(listOf(
            card(Rank.TWO, Suit.SPADES), card(Rank.TWO, Suit.HEARTS), card(Rank.NINE, Suit.DIAMONDS),
            card(Rank.JACK, Suit.CLUBS), card(Rank.KING, Suit.SPADES),
            card(Rank.THREE, Suit.HEARTS), card(Rank.SEVEN, Suit.CLUBS)
        ))
        val flush = HandEvaluator.evaluate(listOf(
            card(Rank.TWO, Suit.CLUBS), card(Rank.FIVE, Suit.CLUBS), card(Rank.NINE, Suit.CLUBS),
            card(Rank.JACK, Suit.CLUBS), card(Rank.KING, Suit.CLUBS),
            card(Rank.ACE, Suit.HEARTS), card(Rank.THREE, Suit.DIAMONDS)
        ))
        // A flush must always beat a pair regardless of any tiebreaker values
        assertTrue(pair < flush)
    }

    @Test
    fun exactlyFiveCardsStillWorks() {
        // Evaluator must also handle exactly 5 cards (e.g. testing pre-flop combos isn't realistic,
        // but the function's contract allows 5-7, and 5 is the boundary case worth covering)
        val hand = listOf(
            card(Rank.TEN, Suit.SPADES), card(Rank.TEN, Suit.HEARTS), card(Rank.TEN, Suit.DIAMONDS),
            card(Rank.TWO, Suit.CLUBS), card(Rank.THREE, Suit.SPADES)
        )
        val result = HandEvaluator.evaluate(hand)
        assertEquals(HandCategory.THREE_OF_A_KIND, result.category)
    }
}