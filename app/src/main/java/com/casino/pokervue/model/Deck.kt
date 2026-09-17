package com.casino.pokervue.model

class Deck(excluding: Set<Card> = emptySet()) {

    private val cards: MutableList<Card> = buildList {
        for (suit in Suit.entries) {
            for (rank in Rank.entries) {
                val card = Card(rank, suit)
                if (card !in excluding) add(card)
            }
        }
    }.toMutableList()

    val remainingCount: Int
        get() = cards.size

    fun drawRandom(): Card {
        check(cards.isNotEmpty()) { "Cannot draw from an empty deck" }
        val index = cards.indices.random()
        return cards.removeAt(index)
    }

    fun drawRandom(n: Int): List<Card> {
        require(n <= cards.size) { "Not enough cards left in deck (requested $n, have ${cards.size})" }
        return List(n) { drawRandom() }
    }
}