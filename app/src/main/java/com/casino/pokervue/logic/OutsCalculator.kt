package com.casino.pokervue.logic

import com.casino.pokervue.model.Card
import com.casino.pokervue.model.Rank
import com.casino.pokervue.model.Suit

object OutsCalculator {
    fun calculateOuts(holeCards: List<Card>, boardCards: List<Card>): List<Card> {
        if (holeCards.size != 2) return emptyList()
        if (boardCards.size !in 3..4) return emptyList()

        val known = (holeCards + boardCards).toSet()
        val currentBest = HandEvaluator.evaluate(holeCards + boardCards)

        val allUnseenCards = buildList {
            for (suit in Suit.entries) {
                for (rank in Rank.entries) {
                    val card = Card(rank, suit)
                    if (card !in known) add(card)
                }
            }
        }

        return allUnseenCards.filter { candidate ->
            val newBest = HandEvaluator.evaluate(holeCards + boardCards + candidate)
            newBest.category.strength > currentBest.category.strength
        }
    }
}