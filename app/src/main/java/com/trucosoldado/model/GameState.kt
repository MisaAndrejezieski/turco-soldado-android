package com.trucosoldado.model

data class PlayedCard(
    val playerId: Int,
    val card: Card
)

data class Player(
    val id: Int,
    val name: String,
    val isAI: Boolean
)

data class GameState(
    val scores: List<Int> = listOf(0, 0, 0),
    val resets: List<Int> = listOf(0, 0, 0),
    val handsPlayed: Int = 0,
    
    val vira: Card? = null,
    val manilhaRank: String? = null,
    val playerHands: List<List<Card>> = listOf(emptyList(), emptyList(), emptyList()),
    
    val currentTrick: Int = 0, // 0, 1, 2
    val starterPlayer: Int = 0,
    val turnPlayer: Int = 0,
    val trickPlayedCards: List<PlayedCard> = emptyList(),
    val trickWinners: List<Int?> = emptyList(), // player id or null if tied
    
    val isGlobalEscura: Boolean = false,
    val individualEscura: List<Boolean> = listOf(false, false, false),
    
    val isHandInProgress: Boolean = false,
    val isProcessingTrick: Boolean = false,
    val matchWinner: Int? = null,
    
    val punishedPlayersOnEnd: List<String> = emptyList(),
    val showSlapOverlay: Boolean = false,
    val showVictoryOverlay: Boolean = false,
    val showRulesModal: Boolean = false,
    val showAllyHand: Boolean = false,
    val showNannyIcon: Boolean = false,
    val activeTaunt: String? = null,
    val currentAnnouncement: String? = null,
    
    val gameLog: List<String> = listOf("Iniciando a partida de Truco Soldado...")
)
