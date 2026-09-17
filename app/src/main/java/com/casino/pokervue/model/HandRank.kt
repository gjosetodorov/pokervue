package com.casino.pokervue.model

enum class HandCategory(val strength: Int) {
    HIGH_CARD(1),
    PAIR(2),
    TWO_PAIR(3),
    THREE_OF_A_KIND(4),
    STRAIGHT(5),
    FLUSH(6),
    FULL_HOUSE(7),
    FOUR_OF_A_KIND(8),
    STRAIGHT_FLUSH(9)
}

val HandRank.isRoyalFlush: Boolean
    get() = category == HandCategory.STRAIGHT_FLUSH && tiebreakers.firstOrNull() == 14

data class HandRank(
    val category: HandCategory,
    val tiebreakers: List<Int>
) : Comparable<HandRank> {
    override fun compareTo(other: HandRank): Int {
        val categoryCompare = category.strength.compareTo(other.category.strength)
        if (categoryCompare != 0) return categoryCompare

        for (i in tiebreakers.indices) {
            val cmp = tiebreakers[i].compareTo(other.tiebreakers.getOrElse(i) { 0 })
            if (cmp != 0) return cmp
        }
        return 0
    }
}