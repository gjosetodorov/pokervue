package com.casino.pokervue.logic

import com.casino.pokervue.model.Card

data class PartialHand(val name: String, val cards: List<Card>)

object PartialHandDetector {
    fun detect(cards: List<Card>): PartialHand? {
        if (cards.size < 2) return null

        val groupedByRank = cards.groupBy { it.rank }
        val groups = groupedByRank.values.sortedByDescending { it.size }

        val trips = groups.firstOrNull { it.size == 3 }
        if (trips != null) {
            return PartialHand("Three of a Kind", trips)
        }

        val pairs = groups.filter { it.size == 2 }
        if (pairs.size >= 2) {
            val twoPairCards = pairs.take(2).flatten()
            return PartialHand("Two Pair", twoPairCards)
        }
        if (pairs.size == 1) {
            return PartialHand("Pair", pairs[0])
        }

        return null
    }
}