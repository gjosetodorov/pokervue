package com.casino.pokervue.logic

import com.casino.pokervue.model.Card
import com.casino.pokervue.model.HandCategory
import com.casino.pokervue.model.HandRank

data class BestHand(
    val rank: HandRank,
    val cards: List<Card>
)

object HandEvaluator {

    fun evaluate(cards: List<Card>): HandRank = evaluateBestHand(cards).rank

    fun evaluateBestHand(cards: List<Card>): BestHand {
        require(cards.size in 5..7) { "Evaluator needs 5-7 cards, got ${cards.size}" }
        val best = combinations(cards, 5)
            .map { hand -> BestHand(evaluateExactlyFive(hand), hand) }
            .maxByOrNull { it.rank }
            ?: error("No combinations found")

        return best.copy(cards = relevantCards(best.cards, best.rank))
    }

    private fun relevantCards(hand: List<Card>, rank: HandRank): List<Card> {
        return when (rank.category) {
            HandCategory.HIGH_CARD -> emptyList()

            HandCategory.PAIR -> {
                val pairRank = rank.tiebreakers[0]
                hand.filter { it.rank.value == pairRank }
            }

            HandCategory.TWO_PAIR -> {
                val highPair = rank.tiebreakers[0]
                val lowPair = rank.tiebreakers[1]
                hand.filter { it.rank.value == highPair || it.rank.value == lowPair }
            }

            HandCategory.THREE_OF_A_KIND -> {
                val tripRank = rank.tiebreakers[0]
                hand.filter { it.rank.value == tripRank }
            }

            HandCategory.FOUR_OF_A_KIND -> {
                val quadRank = rank.tiebreakers[0]
                hand.filter { it.rank.value == quadRank }
            }

            HandCategory.FULL_HOUSE -> {
                val tripRank = rank.tiebreakers[0]
                val pairRank = rank.tiebreakers[1]
                hand.filter { it.rank.value == tripRank || it.rank.value == pairRank }
            }

            HandCategory.STRAIGHT, HandCategory.FLUSH, HandCategory.STRAIGHT_FLUSH -> hand
        }
    }

    private fun evaluateExactlyFive(hand: List<Card>): HandRank {
        val ranks = hand.map { it.rank.value }.sortedDescending()
        val suits = hand.map { it.suit }

        val isFlush = suits.all { it == suits[0] }
        val straightHigh = straightHighCard(ranks)
        val isStraight = straightHigh != null

        val rankCounts = ranks.groupingBy { it }.eachCount()
        val countsDescending = rankCounts.entries.sortedWith(
            compareByDescending<Map.Entry<Int, Int>> { it.value }.thenByDescending { it.key }
        )

        return when {
            isStraight && isFlush -> HandRank(HandCategory.STRAIGHT_FLUSH, listOf(straightHigh!!))

            countsDescending[0].value == 4 -> {
                val quadRank = countsDescending[0].key
                val kicker = ranks.first { it != quadRank }
                HandRank(HandCategory.FOUR_OF_A_KIND, listOf(quadRank, kicker))
            }

            countsDescending[0].value == 3 && countsDescending[1].value == 2 -> {
                HandRank(HandCategory.FULL_HOUSE, listOf(countsDescending[0].key, countsDescending[1].key))
            }

            isFlush -> HandRank(HandCategory.FLUSH, ranks)

            isStraight -> HandRank(HandCategory.STRAIGHT, listOf(straightHigh!!))

            countsDescending[0].value == 3 -> {
                val tripRank = countsDescending[0].key
                val kickers = ranks.filter { it != tripRank }.take(2)
                HandRank(HandCategory.THREE_OF_A_KIND, listOf(tripRank) + kickers)
            }

            countsDescending[0].value == 2 && countsDescending[1].value == 2 -> {
                val highPair = maxOf(countsDescending[0].key, countsDescending[1].key)
                val lowPair = minOf(countsDescending[0].key, countsDescending[1].key)
                val kicker = ranks.first { it != highPair && it != lowPair }
                HandRank(HandCategory.TWO_PAIR, listOf(highPair, lowPair, kicker))
            }

            countsDescending[0].value == 2 -> {
                val pairRank = countsDescending[0].key
                val kickers = ranks.filter { it != pairRank }.take(3)
                HandRank(HandCategory.PAIR, listOf(pairRank) + kickers)
            }

            else -> HandRank(HandCategory.HIGH_CARD, ranks)
        }
    }

    private fun straightHighCard(ranksDescending: List<Int>): Int? {
        val distinct = ranksDescending.distinct()
        if (distinct.size != 5) return null
        if (distinct[0] - distinct[4] == 4) return distinct[0]
        if (distinct == listOf(14, 5, 4, 3, 2)) return 5
        return null
    }

    private fun <T> combinations(items: List<T>, k: Int): List<List<T>> {
        if (k == 0) return listOf(emptyList())
        if (items.isEmpty()) return emptyList()
        val head = items.first()
        val tail = items.drop(1)
        val withHead = combinations(tail, k - 1).map { listOf(head) + it }
        val withoutHead = combinations(tail, k)
        return withHead + withoutHead
    }
}