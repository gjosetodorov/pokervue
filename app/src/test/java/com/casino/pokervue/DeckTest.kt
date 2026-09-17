package com.casino.pokervue

import com.casino.pokervue.model.Card
import com.casino.pokervue.model.Deck
import com.casino.pokervue.model.Rank
import com.casino.pokervue.model.Suit
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse

class DeckTest {
    @Test
    fun deckExcludesKnownCards() {
        val known = setOf(Card(Rank.ACE, Suit.SPADES), Card(Rank.KING, Suit.HEARTS))
        val deck = Deck(excluding = known)
        assertEquals(50, deck.remainingCount)
    }

    @Test
    fun drawingReducesRemainingCount() {
        val deck = Deck()
        val drawn = deck.drawRandom(5)
        assertEquals(5, drawn.size)
        assertEquals(47, deck.remainingCount)
        assertEquals(5, drawn.toSet().size) // confirms no duplicates among the 5 drawn
    }

    @Test
    fun neverDrawsExcludedCards() {
        val excludedCard = Card(Rank.ACE, Suit.SPADES)
        repeat(100) {
            val deck = Deck(excluding = setOf(excludedCard)) // fresh deck each time
            assertFalse(deck.drawRandom() == excludedCard)
        }
    }
}