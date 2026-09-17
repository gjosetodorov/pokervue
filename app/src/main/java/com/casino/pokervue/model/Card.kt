package com.casino.pokervue.model

enum class Suit(val symbol: String, val resName: String) {
    SPADES("♠", "spades"),
    HEARTS("♥", "hearts"),
    DIAMONDS("♦", "diamonds"),
    CLUBS("♣", "clubs")
}

enum class CardColor {
    RED, BLACK
}

enum class Rank(val symbol: String, val resName: String, val value: Int) {
    TWO("2", "2", 2),
    THREE("3", "3", 3),
    FOUR("4", "4", 4),
    FIVE("5", "5", 5),
    SIX("6", "6", 6),
    SEVEN("7", "7", 7),
    EIGHT("8", "8", 8),
    NINE("9", "9", 9),
    TEN("10", "10", 10),
    JACK("J", "jack", 11),
    QUEEN("Q", "queen", 12),
    KING("K", "king", 13),
    ACE("A", "ace", 14)
}

data class Card(
    val rank: Rank,
    val suit: Suit
) {
    val color: CardColor
        get() = if (suit == Suit.HEARTS || suit == Suit.DIAMONDS) CardColor.RED else CardColor.BLACK
}