package com.trucosoldado.model

enum class Suit(val title: String, val symbol: String, val colorHex: String, val power: Int) {
    OUROS("Ouros", "♦", "#DC2626", 1),       // Red
    ESPADAS("Espadas", "♠", "#0F172A", 2),   // Slate 900
    COPAS("Copas", "♥", "#DC2626", 3),       // Red
    PAUS("Paus", "♣", "#0F172A", 4)          // Slate 900
}

data class Card(
    val rank: String,
    val suit: Suit,
    val id: String = "${rank}-${suit.name}"
) {
    fun getCardValue(manilhaRank: String): Int {
        return if (rank == manilhaRank) {
            100 + suit.power
        } else {
            RANK_ORDER.indexOf(rank)
        }
    }

    companion object {
        val RANK_ORDER = listOf("4", "5", "6", "7", "Q", "J", "K", "A", "2", "3")

        fun createDeck(): List<Card> {
            val deck = mutableListOf<Card>()
            for (r in RANK_ORDER) {
                for (s in Suit.values()) {
                    deck.add(Card(rank = r, suit = s))
                }
            }
            return deck
        }

        fun getManilhaRank(viraRank: String): String {
            val index = RANK_ORDER.indexOf(viraRank)
            val nextIndex = (index + 1) % RANK_ORDER.size
            return RANK_ORDER[nextIndex]
        }
    }
}
