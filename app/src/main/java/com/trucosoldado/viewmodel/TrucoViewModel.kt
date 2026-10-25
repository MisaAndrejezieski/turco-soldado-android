package com.trucosoldado.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trucosoldado.model.Card
import com.trucosoldado.model.GameState
import com.trucosoldado.model.PlayedCard
import com.trucosoldado.model.Player
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrucoViewModel : ViewModel() {
    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    val players = listOf(
        Player(0, "Porco 1 (Você)", false),
        Player(1, "Porco 2 (IA 1)", true),
        Player(2, "Porco 3 (IA 2)", true)
    )

    init {
        initGame()
    }

    fun initGame() {
        _state.value = GameState(
            gameLog = listOf("Partida iniciada! Boa sorte no Truco Soldado.")
        )
        startNewHand()
    }

    fun resetGame() {
        initGame()
    }

    fun setRulesModalOpen(open: Boolean) {
        _state.update { it.copy(showRulesModal = open) }
    }

    fun dismissSlapOverlay() {
        _state.update { it.copy(showSlapOverlay = false, showVictoryOverlay = true) }
    }

    fun checkEscuraConditions() {
        val s = _state.value.scores
        val isGlobal = (s[0] == s[1] && s[1] == s[2] && s[0] > 0)
        val indEscura = listOf(s[0] == 11, s[1] == 11, s[2] == 11)
        _state.update { it.copy(isGlobalEscura = isGlobal, individualEscura = indEscura) }
    }

    fun startNewHand() {
        val current = _state.value
        if (current.isProcessingTrick || current.matchWinner != null) return

        val newHandsPlayed = current.handsPlayed + 1
        checkEscuraConditions()

        val deck = Card.createDeck().shuffled().toMutableList()
        val vira = deck.removeAt(deck.size - 1)
        val manilhaRank = Card.getManilhaRank(vira.rank)

        val playerHands = listOf(
            listOf(deck.removeAt(0), deck.removeAt(0), deck.removeAt(0)),
            listOf(deck.removeAt(0), deck.removeAt(0), deck.removeAt(0)),
            listOf(deck.removeAt(0), deck.removeAt(0), deck.removeAt(0))
        )

        val starterPlayer = if (newHandsPlayed > 1) (current.starterPlayer + 1) % 3 else current.starterPlayer

        _state.update {
            it.copy(
                handsPlayed = newHandsPlayed,
                vira = vira,
                manilhaRank = manilhaRank,
                playerHands = playerHands,
                currentTrick = 0,
                starterPlayer = starterPlayer,
                turnPlayer = starterPlayer,
                trickPlayedCards = emptyList(),
                trickWinners = emptyList(),
                isHandInProgress = true,
                isProcessingTrick = false
            )
        }

        logMessage("--- Nova Mão (${newHandsPlayed}ª) --- Vira é [${vira.rank}${vira.suit.symbol}]. Manilha: $manilhaRank")

        if (players[starterPlayer].isAI) {
            scheduleAITurn()
        }
    }

    fun playCard(playerId: Int, cardIndex: Int) {
        val current = _state.value
        if (!current.isHandInProgress || current.isProcessingTrick) return
        if (playerId != current.turnPlayer) return

        val hand = current.playerHands[playerId].toMutableList()
        if (cardIndex < 0 || cardIndex >= hand.size) return

        val playedCard = hand.removeAt(cardIndex)
        val newPlayerHands = current.playerHands.toMutableList()
        newPlayerHands[playerId] = hand

        val newPlayedCards = current.trickPlayedCards + PlayedCard(playerId, playedCard)

        _state.update {
            it.copy(
                playerHands = newPlayerHands,
                trickPlayedCards = newPlayedCards
            )
        }

        logMessage("${players[playerId].name} jogou [${playedCard.rank}${playedCard.suit.symbol}]")

        if (newPlayedCards.size == 3) {
            _state.update { it.copy(isProcessingTrick = true) }
            viewModelScope.launch {
                delay(1000)
                resolveTrick()
            }
        } else {
            val nextTurn = (current.turnPlayer + 1) % 3
            _state.update { it.copy(turnPlayer = nextTurn) }
            if (players[nextTurn].isAI) {
                scheduleAITurn()
            }
        }
    }

    private fun resolveTrick() {
        val current = _state.value
        val played = current.trickPlayedCards
        val manilha = current.manilhaRank ?: ""

        var highestVal = -1
        var winnerPlayerId: Int? = null
        var isTie = false

        for (item in played) {
            val valCard = item.card.getCardValue(manilha)
            if (valCard > highestVal) {
                highestVal = valCard
                winnerPlayerId = item.playerId
                isTie = false
            } else if (valCard == highestVal) {
                isTie = true
            }
        }

        val trickPoints = listOf(3, 2, 1)
        val pointsAwarded = trickPoints[current.currentTrick]
        val newTrickWinners = current.trickWinners.toMutableList()

        if (isTie || winnerPlayerId == null) {
            logMessage("Vaza ${current.currentTrick + 1} empatou! Ninguém pontua.")
            newTrickWinners.add(null)
            _state.update { it.copy(trickWinners = newTrickWinners) }
        } else {
            logMessage("Vaza ${current.currentTrick + 1} vencida por ${players[winnerPlayerId].name}! (+$pointsAwarded tentos)")
            newTrickWinners.add(winnerPlayerId)
            _state.update { it.copy(trickWinners = newTrickWinners) }
            addPointsAndCheckBust(winnerPlayerId, pointsAwarded)
        }

        val nextTrickNum = current.currentTrick + 1

        viewModelScope.launch {
            delay(1200)
            _state.update {
                it.copy(
                    trickPlayedCards = emptyList()
                )
            }

            if (nextTrickNum < 3) {
                val nextTurn = if (!isTie && winnerPlayerId != null) winnerPlayerId else current.starterPlayer
                _state.update {
                    it.copy(
                        currentTrick = nextTrickNum,
                        turnPlayer = nextTurn,
                        isProcessingTrick = false
                    )
                }
                if (players[nextTurn].isAI) {
                    scheduleAITurn()
                }
            } else {
                finishHand()
            }
        }
    }

    private fun addPointsAndCheckBust(playerId: Int, points: Int) {
        val current = _state.value
        val scores = current.scores.toMutableList()
        val resets = current.resets.toMutableList()

        scores[playerId] += points

        if (scores[playerId] >= 13) {
            scores[playerId] = 0
            resets[playerId]++
            logMessage("💥 ESTOURO! ${players[playerId].name} atingiu 13+ tentos! Voltou para 0.")
        } else if (scores[playerId] == 12) {
            logMessage("🎯 ${players[playerId].name} CRAVOU EXATAMENTE 12 TENTOS!")
        }

        _state.update {
            it.copy(scores = scores, resets = resets)
        }
    }

    private fun finishHand() {
        val current = _state.value
        _state.update { it.copy(isHandInProgress = false, isProcessingTrick = false) }

        val winners = mutableListOf<Int>()
        for (i in 0..2) {
            if (current.scores[i] == 12) winners.add(i)
        }

        if (winners.isNotEmpty()) {
            val winnerId = winners[0]
            handleMatchEnd(winnerId)
        } else {
            logMessage("Mão finalizada! Clique em 'NOVA MÃO'.")
        }
    }

    private fun handleMatchEnd(winnerId: Int) {
        val current = _state.value
        val punished = mutableListOf<String>()
        for (i in 0..2) {
            if (i != winnerId && current.scores[i] == 0) {
                punished.add(players[i].name)
            }
        }

        if (punished.isNotEmpty()) {
            viewModelScope.launch {
                delay(800)
                _state.update {
                    it.copy(
                        matchWinner = winnerId,
                        punishedPlayersOnEnd = punished,
                        showSlapOverlay = true
                    )
                }
            }
        } else {
            _state.update {
                it.copy(
                    matchWinner = winnerId,
                    showVictoryOverlay = true
                )
            }
        }
    }

    private fun scheduleAITurn() {
        viewModelScope.launch {
            delay(800)
            executeAITurn()
        }
    }

    private fun executeAITurn() {
        val current = _state.value
        if (!current.isHandInProgress || current.isProcessingTrick) return
        val aiId = current.turnPlayer
        if (!players[aiId].isAI) return

        val hand = current.playerHands[aiId]
        if (hand.isEmpty()) return

        val isEscura = current.isGlobalEscura || current.individualEscura[aiId]
        val cardIndex = if (isEscura) {
            (0 until hand.size).random()
        } else {
            selectBestAICard(aiId, hand)
        }

        playCard(aiId, cardIndex)
    }

    private fun selectBestAICard(aiId: Int, hand: List<Card>): Int {
        val current = _state.value
        val manilha = current.manilhaRank ?: ""
        val played = current.trickPlayedCards

        val evaluated = hand.mapIndexed { index, card ->
            Triple(index, card, card.getCardValue(manilha))
        }.sortedBy { it.third }

        if (played.isEmpty()) {
            return evaluated[0].first
        }

        var highestVal = -1
        var highestPlayer: Int? = null
        for (item in played) {
            val v = item.card.getCardValue(manilha)
            if (v > highestVal) {
                highestVal = v
                highestPlayer = item.playerId
            }
        }

        val isTargetLeading = highestPlayer != null && current.scores[highestPlayer] in 8..11
        if (played.size == 1 && isTargetLeading) {
            // Coalition: play weakest card
            return evaluated[0].first
        }

        val winningCards = evaluated.filter { it.third > highestVal }
        return if (winningCards.isNotEmpty()) {
            winningCards[0].first
        } else {
            evaluated[0].first
        }
    }

    private fun logMessage(msg: String) {
        _state.update {
            it.copy(gameLog = it.gameLog + msg)
        }
    }
}
