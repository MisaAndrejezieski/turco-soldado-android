package com.trucosoldado.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trucosoldado.model.Card
import com.trucosoldado.model.GameState
import com.trucosoldado.model.Player
import com.trucosoldado.ui.components.CardItemView
import com.trucosoldado.viewmodel.TrucoViewModel

@Composable
fun TrucoScreen(viewModel: TrucoViewModel) {
    val state by viewModel.state.collectAsState()
    val players = viewModel.players
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1D13))
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // HEADER PRINCIPAL (TOPO)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xCC064E3B), RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0x88047857), RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFFEC4899), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🐷", fontSize = 18.sp)
                    }
                    Column {
                        Text(
                            text = "TRUCO SOLDADO",
                            color = Color(0xFFFBBF24),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "• Misa, Adriano & Richard",
                            color = Color(0xFFA7F3D0),
                            fontSize = 8.sp
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    val maxOpponentScore = listOf(state.scores[1], state.scores[2]).maxOrNull() ?: 0
                    if (maxOpponentScore >= 5) {
                        val targetId = if (state.scores[1] >= state.scores[2]) 1 else 2
                        val allyId = if (targetId == 1) 2 else 1
                        Button(
                            onClick = { viewModel.toggleShowAllyHand() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text(if (state.showAllyHand) "Ocultar Aliado" else "🤝 Ver ${players[allyId].name.substringBefore(" ")}", fontSize = 8.sp)
                        }
                    }
                    Button(
                        onClick = { viewModel.setSetupDialogVisible(true) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("👤 Perfil", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { viewModel.setRulesModalOpen(true) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(Icons.Default.Book, contentDescription = null, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Regras", fontSize = 9.sp)
                    }
                    Button(
                        onClick = { viewModel.resetGame() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB91C1C)),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Reiniciar", fontSize = 9.sp)
                    }
                }
            }

            // --- BANNER DE MENSAGEM / ANÚNCIO TEMPORÁRIO (Aparece e some rápido) ---
            state.currentAnnouncement?.let { announcement ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xEE1E293B), RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFF34D399), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📢 $announcement",
                        color = Color(0xFFA7F3D0),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // --- LAYOUT RESPONSIVO (Retrato vs Paisagem) ---
            if (isLandscape) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Mesa de Feltro (Esquerda)
                    Box(
                        modifier = Modifier
                            .weight(1.3f)
                            .fillMaxHeight()
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0xFF1E4D2B), Color(0xFF0D2916), Color(0xFF05140A))
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .border(4.dp, Color(0xFF2D1806), RoundedCornerShape(20.dp))
                            .padding(8.dp)
                    ) {
                        TrucoTableContent(state = state, players = players, viewModel = viewModel)
                    }

                    // Cartas e Controles do Jogador (Direita)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(Color(0x880F172A), RoundedCornerShape(20.dp))
                            .padding(8.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PlayerHandAndControls(state = state, viewModel = viewModel)
                    }
                }
            } else {
                // Retrato (De pé)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0xFF1E4D2B), Color(0xFF0D2916), Color(0xFF05140A))
                                ),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .border(6.dp, Color(0xFF2D1806), RoundedCornerShape(24.dp))
                            .padding(10.dp)
                    ) {
                        TrucoTableContent(state = state, players = players, viewModel = viewModel)
                    }

                    PlayerHandAndControls(state = state, viewModel = viewModel)
                }
            }
        }

        // --- DIÁLOGO DE CONFIGURAÇÃO DE PERFIL / JOGADOR ---
        if (state.showSetupDialog) {
            PlayerSetupDialog(viewModel = viewModel, currentState = state)
        }

        // --- MODAL DE REGRAS ---
        if (state.showRulesModal) {
            AlertDialog(
                onDismissRequest = { viewModel.setRulesModalOpen(false) },
                title = { Text("Regras: Truco Soldado (PR)", color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("1. Cartas e Manilha: 4 < 5 < 6 < 7 < Q < J < K < A < 2 < 3. Manilha é a carta acima da Vira. Desempate por naipe: Ouros < Espadas < Copas < Paus.", color = Color.LightGray, fontSize = 11.sp)
                        Text("2. Pontos por Vaza: 1ª vaza vale 3 tentos, 2ª vale 2 tentos e 3ª vale 1 tento.", color = Color.LightGray, fontSize = 11.sp)
                        Text("3. Estouro e Vitória: 13+ tentos estoura e zera. Exatamente 12 tentos vence a partida!", color = Color.LightGray, fontSize = 11.sp)
                        Text("4. A Escura: Global ou Individual (11 tentos).", color = Color.LightGray, fontSize = 11.sp)
                        Text("5. Tapa na Vaca: Se alguém vencer com 12 e o oponente terminar com 0, o oponente toma o tapa!", color = Color.LightGray, fontSize = 11.sp)
                    }
                },
                confirmButton = {
                    Button(onClick = { viewModel.setRulesModalOpen(false) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706))) {
                        Text("Entendido!")
                    }
                },
                containerColor = Color(0xFF0F172A)
            )
        }

        // --- OVERLAY DE TAPA NA VACA ---
        if (state.showSlapOverlay) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("💥 O TAPA NA VACA!", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("${state.playerName} venceu a partida cravando 12 tentos!", color = Color.White, fontSize = 12.sp)
                        Text("Punidos no zero (Tomaram o Tapa na Vaca):", color = Color(0xFFA7F3D0), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        state.punishedPlayersOnEnd.forEach { p ->
                            Text("• $p", color = Color(0xFFF87171), fontSize = 12.sp)
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { viewModel.dismissSlapOverlay() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEAB308))) {
                        Text("VER VITÓRIA", color = Color.Black, fontWeight = FontWeight.Black)
                    }
                },
                containerColor = Color(0xFF064E3B)
            )
        }

        // --- OVERLAY DE VITÓRIA ---
        if (state.showVictoryOverlay) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("🏆 FIM DE JOGO!", color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                text = {
                    Text("Partida finalizada com sucesso! Vamos para a revanche?", color = Color.White, fontSize = 12.sp)
                },
                confirmButton = {
                    Button(onClick = { viewModel.resetGame() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEAB308))) {
                        Text("JOGAR NOVAMENTE", color = Color.Black, fontWeight = FontWeight.Black)
                    }
                },
                containerColor = Color(0xFF064E3B)
            )
        }
    }
}

@Composable
fun PlayerSetupDialog(viewModel: TrucoViewModel, currentState: GameState) {
    var tempName by remember { mutableStateOf(currentState.playerName) }
    var tempAvatar by remember { mutableStateOf(currentState.playerAvatar) }
    val avatars = listOf("🐢🧘", "🐷", "🪖", "🧙‍♂️", "🤠", "🤖", "🦊", "🦁")

    AlertDialog(
        onDismissRequest = { viewModel.setSetupDialogVisible(false) },
        title = { Text("Escolher Jogador & Avatar", color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Personalize seu perfil para a partida de Truco Soldado:", color = Color.LightGray, fontSize = 12.sp)
                
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    label = { Text("Seu Nome") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFBBF24),
                        unfocusedBorderColor = Color(0xFF10B981),
                        focusedLabelColor = Color(0xFFFBBF24),
                        unfocusedLabelColor = Color(0xFF10B981)
                    )
                )

                Text("Escolha seu Avatar:", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    avatars.forEach { av ->
                        val selected = av == tempAvatar
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(if (selected) Color(0xFF10B981) else Color(0xFF1E293B), CircleShape)
                                .border(2.dp, if (selected) Color(0xFFFBBF24) else Color.Transparent, CircleShape)
                                .clickable { tempAvatar = av },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(av, fontSize = 18.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { viewModel.updatePlayerConfig(tempName.ifBlank { "MISA" }, tempAvatar) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEAB308))
            ) {
                Text("SALVAR & JOGAR", color = Color.Black, fontWeight = FontWeight.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.setSetupDialogVisible(false) }) {
                Text("Cancelar", color = Color.LightGray)
            }
        },
        containerColor = Color(0xFF0F172A)
    )
}

@Composable
fun TrucoTableContent(state: GameState, players: List<Player>, viewModel: TrucoViewModel) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Indicador de Escura Global (Topo Central)
        if (state.isGlobalEscura) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .background(Color(0xEE581C87), RoundedCornerShape(50.dp))
                    .border(1.dp, Color(0xFFC084FC), RoundedCornerShape(50.dp))
                    .padding(horizontal = 10.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "👁️ Mão na Escura Global!",
                    color = Color(0xFFE9D5FF),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Balão de Fala / Taunt & Nanny Icon
        state.activeTaunt?.let { taunt ->
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFFDC2626), RoundedCornerShape(12.dp))
                        .border(2.dp, Color(0xFFFBBF24), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "🗣️ \"$taunt\"",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
                if (state.showNannyIcon) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF047857), RoundedCornerShape(50.dp))
                            .border(1.dp, Color(0xFFFBBF24), RoundedCornerShape(50.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "👩‍🍼 🐷 (Babá de Salvação!)",
                            color = Color(0xFFFBBF24),
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }

        val maxOpponentScore = listOf(state.scores[1], state.scores[2]).maxOrNull() ?: 0
        val targetId = if (state.scores[1] >= state.scores[2]) 1 else 2
        val allyId = if (targetId == 1) 2 else 1

        // --- PORCO 2 (ADRIANO - TOPO ESQUERDA) ---
        Box(modifier = Modifier.align(Alignment.TopStart)) {
            OpponentTableSection(
                player = players[1],
                score = state.scores[1],
                resets = state.resets[1],
                hand = state.playerHands.getOrNull(1),
                isTurn = state.turnPlayer == 1,
                showCards = state.showAllyHand && allyId == 1,
                manilhaRank = state.manilhaRank
            )
        }

        // --- PORCO 3 (RICHARD - TOPO DIREITA) ---
        Box(modifier = Modifier.align(Alignment.TopEnd)) {
            OpponentTableSection(
                player = players[2],
                score = state.scores[2],
                resets = state.resets[2],
                hand = state.playerHands.getOrNull(2),
                isTurn = state.turnPlayer == 2,
                showCards = state.showAllyHand && allyId == 2,
                manilhaRank = state.manilhaRank
            )
        }

        // --- CENTRO DA MESA: BADGE DA VAZA, DECK, VIRA E CARTAS JOGADAS ---
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val points = when (state.currentTrick) {
                0 -> 3
                1 -> 2
                else -> 1
            }
            Box(
                modifier = Modifier
                    .background(Color(0xCC064E3B), RoundedCornerShape(50.dp))
                    .border(1.dp, Color(0xFF10B981), RoundedCornerShape(50.dp))
                    .padding(horizontal = 10.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "VAZA ${state.currentTrick + 1}: VALE $points ${if (points == 1) "TENTO" else "TENTOS"}",
                    color = Color(0xFFFBBF24),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                contentAlignment = Alignment.Center
            ) {
                // 1. Deck e Vira
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy((-14).dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(50.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Brush.linearGradient(listOf(Color(0xFFF472B6), Color(0xFFDB2777))))
                            .border(1.dp, Color(0xFFFBCFE8), RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🐷", fontSize = 12.sp)
                    }
                    state.vira?.let { viraCard ->
                        Box(modifier = Modifier.size(width = 36.dp, height = 50.dp)) {
                            CardItemView(card = viraCard, isManilha = false)
                        }
                    }
                }

                // 2. Cartas Jogadas na Vaza
                val p0Played = state.trickPlayedCards.find { it.playerId == 0 }
                val p1Played = state.trickPlayedCards.find { it.playerId == 1 }
                val p2Played = state.trickPlayedCards.find { it.playerId == 2 }

                if (p1Played != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(x = (-28).dp, y = (-10).dp)
                            .size(width = 38.dp, height = 54.dp)
                    ) {
                        CardItemView(card = p1Played.card, isManilha = p1Played.card.rank == state.manilhaRank)
                    }
                }

                if (p2Played != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(x = 28.dp, y = (-10).dp)
                            .size(width = 38.dp, height = 54.dp)
                    ) {
                        CardItemView(card = p2Played.card, isManilha = p2Played.card.rank == state.manilhaRank)
                    }
                }

                if (p0Played != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(y = 14.dp)
                            .size(width = 38.dp, height = 54.dp)
                    ) {
                        CardItemView(card = p0Played.card, isManilha = p0Played.card.rank == state.manilhaRank)
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerHandAndControls(state: GameState, viewModel: TrucoViewModel) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Cartas na mão do jogador humano
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val humanHand = state.playerHands.getOrNull(0) ?: emptyList()
            val isEscura = state.isGlobalEscura || state.individualEscura[0]

            humanHand.forEachIndexed { index, card ->
                Box(modifier = Modifier.size(width = 50.dp, height = 70.dp)) {
                    CardItemView(
                        card = if (isEscura) null else card,
                        isManilha = card.rank == state.manilhaRank,
                        isBack = isEscura,
                        onClick = {
                            if (state.turnPlayer == 0 && state.isHandInProgress && !state.isProcessingTrick) {
                                viewModel.playCard(0, index)
                            }
                        }
                    )
                }
            }
        }

        // Painel de Informações do Jogador & Botão Nova Mão
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xE60F172A), RoundedCornerShape(14.dp))
                .border(
                    width = if (state.turnPlayer == 0) 2.dp else 1.dp,
                    color = if (state.turnPlayer == 0) Color(0xFFFBBF24) else Color(0x6610B981),
                    shape = RoundedCornerShape(14.dp)
                )
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(Color(0xFFEC4899), CircleShape)
                        .border(1.dp, Color(0xFFFBBF24), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.playerAvatar, fontSize = 12.sp)
                }
                Column {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(state.playerName, color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        if (state.individualEscura[0]) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF7E22CE), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 3.dp, vertical = 1.dp)
                            ) {
                                Text("ESCURA", color = Color(0xFFE9D5FF), fontSize = 7.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Tentos: ${state.scores[0]}/12", color = Color(0xFFFBBF24), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("Estouros: ${state.resets[0]}", color = Color(0xFFEF4444), fontSize = 10.sp)
                    }
                }
            }

            Button(
                onClick = { viewModel.startNewHand() },
                enabled = !state.isHandInProgress && state.matchWinner == null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEAB308),
                    disabledContainerColor = Color(0x44EAB308)
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                    Text("NOVA MÃO", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
fun OpponentTableSection(
    player: Player,
    score: Int,
    resets: Int,
    hand: List<Card>?,
    isTurn: Boolean,
    showCards: Boolean,
    manilhaRank: String?
) {
    Column(
        modifier = Modifier
            .background(Color(0xCC064E3B), RoundedCornerShape(12.dp))
            .border(
                width = if (isTurn) 2.dp else 1.dp,
                color = if (isTurn) Color(0xFFFBBF24) else Color(0x66047857),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .background(Color(0xFF1E293B), CircleShape)
                    .border(2.dp, if (isTurn) Color(0xFFFBBF24) else Color(0xFF475569), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("🐗", fontSize = 12.sp)
            }
            Column {
                Text(player.name, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("⭐ $score", color = Color(0xFFFBBF24), fontSize = 9.sp)
                    Text("💀 $resets", color = Color(0xFFEF4444), fontSize = 9.sp)
                }
            }
        }

        Row(
            modifier = Modifier.padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(if (showCards) 2.dp else (-8).dp)
        ) {
            if (showCards && hand != null) {
                hand.forEach { card ->
                    Box(modifier = Modifier.size(width = 26.dp, height = 38.dp)) {
                        CardItemView(card = card, isManilha = card.rank == manilhaRank)
                    }
                }
            } else {
                repeat(hand?.size ?: 0) {
                    OpponentCardBack()
                }
            }
        }
    }
}

@Composable
fun OpponentCardBack() {
    Box(
        modifier = Modifier
            .width(24.dp)
            .height(34.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Brush.linearGradient(listOf(Color(0xFFF472B6), Color(0xFFDB2777))))
            .border(1.dp, Color(0xFFFBCFE8), RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "🐷", fontSize = 9.sp)
    }
}
