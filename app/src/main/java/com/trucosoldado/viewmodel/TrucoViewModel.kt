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
            gameLog = listOf("Partida iniciada! Boa sorte no Truco Soldado (Regra do PR).")
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

        val maxScore = current.scores.maxOrNull() ?: 0
        if (maxScore >= 5) {
            val leaderId = current.scores.indexOf(maxScore)
            logMessage("🤝 COALIZÃO ATIVADA: ${players[leaderId].name} (${maxScore} tentos) é o alvo! Os demais unem forças para sabotá-lo e buscar o Tapa na Vaca.")
        }

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
        val oldScore = current.scores[playerId]
        val scores = current.scores.toMutableList()
        val resets = current.resets.toMutableList()

        scores[playerId] += points
        val newScore = scores[playerId]

        var taunt: String? = null
        if (oldScore == 0 && newScore > 0 && newScore < 12) {
            taunt = "PENSO, NÉ!"
            logMessage("💡 ${players[playerId].name}: \"PENSO, NÉ!\" (Salvou-se do zero!)")
        }
        if (newScore == 4) {
            taunt = "KUAK!"
            logMessage("🐷 ${players[playerId].name}: \"KUAK!\"")
        } else if (listOf(5, 8, 10, 11).contains(newScore)) {
            taunt = "SE FUDEU!"
            logMessage("🔥 ${players[playerId].name}: \"SE FUDEU!\" (Chegou em $newScore tentos!)")
        }

        if (scores[playerId] >= 13) {
            scores[playerId] = 0
            resets[playerId]++
            logMessage("💥 ESTOURO! ${players[playerId].name} atingiu 13+ tentos! Voltou para 0.")
        } else if (scores[playerId] == 12) {
            logMessage("🎯 ${players[playerId].name} CRAVOU EXATAMENTE 12 TENTOS!")
        }

        _state.update {
            it.copy(scores = scores, resets = resets, activeTaunt = taunt)
        }
    }

    fun toggleShowAllyHand() {
        _state.update { it.copy(showAllyHand = !it.showAllyHand) }
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

    /**
     * Lógica Avançada de IA & Coalizão (Truco Soldado - PR):
     * - Alvo Principal: Sabotagem implacável contra adversários avançados (5+, 8, 11 tentos) visando o Tapa na Vaca (0 tentos).
     * - Cooperação Tática: Os oponentes se auxiliam para cobrir, bloquear e deixar o caminho livre para o aliado vencer a vaza em cima do alvo perigoso.
     */
    private fun selectBestAICard(aiId: Int, hand: List<Card>): Int {
        val current = _state.value
        val manilha = current.manilhaRank ?: ""
        val played = current.trickPlayedCards
        val scores = current.scores

        // 1. Avaliar todas as cartas na mão (índice, carta, valor), ordenadas do menor para o maior
        val evaluated = hand.mapIndexed { index, card ->
            Triple(index, card, card.getCardValue(manilha))
        }.sortedBy { it.third }

        // 2. Identificar o Alvo Principal (adversário com maior pontuação e perigoso >= 5)
        val opponents = (0..2).filter { it != aiId }
        val targetPlayerId = opponents.maxByOrNull { scores[it] } ?: opponents.first()
        val targetScore = scores[targetPlayerId]
        val isTargetDangerous = targetScore >= 5

        // Identificar o Aliado (o outro oponente não-alvo)
        val allyPlayerId = opponents.firstOrNull { it != targetPlayerId } ?: -1

        val playedPlayerIds = played.map { it.playerId }
        val targetHasPlayed = playedPlayerIds.contains(targetPlayerId)

        // Descobrir quem está ganhando a vaza até o momento
        var highestVal = -1
        var highestPlayer: Int? = null
        for (item in played) {
            val v = item.card.getCardValue(manilha)
            if (v > highestVal) {
                highestVal = v
                highestPlayer = item.playerId
            }
        }

        val winningCards = evaluated.filter { it.third > highestVal }

        // ==========================================================
        // CENÁRIO 1: IA abre a vaza (primeira a jogar)
        // ==========================================================
        if (played.isEmpty()) {
            return if (isTargetDangerous && evaluated.last().third > 80) {
                // Pressão agressiva com carta forte se o alvo for perigoso
                evaluated.last().first
            } else if (isTargetDangerous) {
                // Poupar cartas fortes, saindo com carta intermediária/baixa
                evaluated[evaluated.size / 2].first
            } else {
                evaluated[evaluated.size / 2].first
            }
        }

        // ==========================================================
        // CENÁRIO 2: O Alvo Perigoso está ganhando a vaza
        // ==========================================================
        if (highestPlayer == targetPlayerId) {
            // SABOTAGEM TOTAL: Tentar roubar a vaza do alvo a todo custo
            return if (winningCards.isNotEmpty()) {
                winningCards.first().first // Menor carta vencedora para bloquear o alvo
            } else {
                evaluated.first().first // Sem carta vencedora, descarta a menor
            }
        }

        // ==========================================================
        // CENÁRIO 3: O Aliado (outro oponente) está ganhando a vaza
        // ==========================================================
        if (highestPlayer != null && highestPlayer == allyPlayerId && isTargetDangerous) {
            // COOPERAÇÃO TÁTICA: Deixar o aliado vencer para barrar o alvo perigoso
            return if (targetHasPlayed) {
                val targetCardVal = played.firstOrNull { it.playerId == targetPlayerId }?.card?.getCardValue(manilha) ?: 0
                if (targetCardVal < highestVal) {
                    // Alvo já jogou e não pode vencer o aliado -> Proteger a vitória do aliado descartando carta baixa!
                    evaluated.first().first
                } else {
                    // Alvo pode ameaçar -> Garantir com carta vencedora se necessário
                    if (winningCards.isNotEmpty()) winningCards.first().first else evaluated.first().first
                }
            } else {
                // Alvo ainda não jogou (joga por último). Se tiver carta muito forte, assegurar; senão, poupar.
                if (winningCards.isNotEmpty() && evaluated.last().third > 85) {
                    winningCards.last().first
                } else {
                    evaluated.first().first
                }
            }
        }

        // ==========================================================
        // CENÁRIO 4: Jogada padrão otimizada
        // ==========================================================
        return if (winningCards.isNotEmpty()) {
            winningCards.first().first
        } else {
            evaluated.first().first
        }
    }

    private fun logMessage(msg: String) {
        _state.update {
            it.copy(gameLog = it.gameLog + msg)
        }
    }
}
