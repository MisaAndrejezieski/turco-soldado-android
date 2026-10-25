package com.trucosoldado.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trucosoldado.ui.components.CardItemView
import com.trucosoldado.viewmodel.TrucoViewModel

@Composable
fun TrucoScreen(viewModel: TrucoViewModel) {
    val state by viewModel.state.collectAsState()
    val players = viewModel.players

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1D13))
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // HEADER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xCC064E3B), RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0x88047857), RoundedCornerShape(16.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFEC4899), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🐷", fontSize = 20.sp)
                    }
                    Column {
                        Text(
                            text = "TRUCO SOLDADO",
                            color = Color(0xFFFBBF24),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Regra do Sul (PR) • 1ª: 3t, 2ª: 2t, 3ª: 1t",
                            color = Color(0xFFA7F3D0),
                            fontSize = 10.sp
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = { viewModel.setRulesModalOpen(true) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Book, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Regras", fontSize = 12.sp)
                    }
                    Button(
                        onClick = { viewModel.resetGame() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB91C1C)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reiniciar", fontSize = 12.sp)
                    }
                }
            }

            // MAIN TABLE AREA
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 4.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF1E4D2B), Color(0xFF0D2916), Color(0xFF05140A))
                        ),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .border(8.dp, Color(0xFF2D1806), RoundedCornerShape(32.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Global Escura Badge
                if (state.isGlobalEscura) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 12.dp)
                            .background(Color(0xE6581C87), RoundedCornerShape(50.dp))
                            .border(2.dp, Color(0xFFC084FC), RoundedCornerShape(50.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "👁️ Mão na Escura Global!",
                            color = Color(0xFFE9D5FF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // AI 1 (Top Left)
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = Color(0xFF1E293B),
                                shape = CircleShape
                            )
                            .border(
                                width = if (state.turnPlayer == 1) 3.dp else 2.dp,
                                color = if (state.turnPlayer == 1) Color(0xFFFBBF24) else Color(0xFF475569),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🐗", fontSize = 22.sp)
                    }
                    Text(players[1].name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("⭐ ${state.scores[1]}", color = Color(0xFFFBBF24), fontSize = 10.sp)
                        Text("|", color = Color.Gray, fontSize = 10.sp)
                        Text("💀 ${state.resets[1]}", color = Color(0xFFEF4444), fontSize = 10.sp)
                    }
                    Row(modifier = Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy((-16).dp)) {
                        repeat(state.playerHands.getOrNull(1)?.size ?: 0) {
                            CardItemView(card = null, isBack = true)
                        }
                    }
                }

                // AI 2 (Top Right)
                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = Color(0xFF1E293B),
                                shape = CircleShape
                            )
                            .border(
                                width = if (state.turnPlayer == 2) 3.dp else 2.dp,
                                color = if (state.turnPlayer == 2) Color(0xFFFBBF24) else Color(0xFF475569),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🐗", fontSize = 22.sp)
                    }
                    Text(players[2].name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("⭐ ${state.scores[2]}", color = Color(0xFFFBBF24), fontSize = 10.sp)
                        Text("|", color = Color.Gray, fontSize = 10.sp)
                        Text("💀 ${state.resets[2]}", color = Color(0xFFEF4444), fontSize = 10.sp)
                    }
                    Row(modifier = Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy((-16).dp)) {
                        repeat(state.playerHands.getOrNull(2)?.size ?: 0) {
                            CardItemView(card = null, isBack = true)
                        }
                    }
                }

                // CENTER: VIRA, DECK & PLAYED CARDS
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Deck & Vira
                    Row(horizontalArrangement = Arrangement.spacedBy((-12).dp)) {
                        CardItemView(card = null, isBack = true)
                        state.vira?.let { viraCard ->
                            CardItemView(
                                card = viraCard,
                                isManilha = false
                            )
                        }
                    }

                    // Played Trick Cards & Badge
                    Box(
                        modifier = Modifier.size(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val points = when (state.currentTrick) {
                            0 -> 3
                            1 -> 2
                            else -> 1
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .background(Color(0xCC064E3B), RoundedCornerShape(50.dp))
                                .border(1.dp, Color(0xFF10B981), RoundedCornerShape(50.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "VAZA ${state.currentTrick + 1}: VALE $points TENTOS",
                                color = Color(0xFFFBBF24),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Render played cards on center
                        state.trickPlayedCards.forEach { played ->
                            val alignment = when (played.playerId) {
                                0 -> Alignment.BottomCenter
                                1 -> Alignment.CenterStart
                                else -> Alignment.CenterEnd
                            }
                            Box(modifier = Modifier.align(alignment)) {
                                CardItemView(
                                    card = played.card,
                                    isManilha = played.card.rank == state.manilhaRank
                                )
                            }
                        }
                    }
                }

                // HUMAN PLAYER (Bottom Center)
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Human Hand Cards
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        val humanHand = state.playerHands.getOrNull(0) ?: emptyList()
                        val isEscura = state.isGlobalEscura || state.individualEscura[0]
                        humanHand.forEachIndexed { index, card ->
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

                    // Human Info Bar
                    Row(
                        modifier = Modifier
                            .background(Color(0xE60F172A), RoundedCornerShape(16.dp))
                            .border(1.dp, Color(0x6610B981), RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF047857), CircleShape)
                                .border(
                                    width = if (state.turnPlayer == 0) 3.dp else 2.dp,
                                    color = if (state.turnPlayer == 0) Color(0xFFFBBF24) else Color(0xFF10B981),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👑", fontSize = 18.sp)
                        }
                        Column {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("Porco 1 (Você)", color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                if (state.individualEscura[0]) {
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFF7E22CE), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text("ESCURA", color = Color(0xFFE9D5FF), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Tentos: ${state.scores[0]}/12", color = Color(0xFFFBBF24), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("Estouros: ${state.resets[0]}", color = Color(0xFFEF4444), fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // FOOTER: LOG & NEXT HAND BUTTON
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Game Log
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color(0xE6064E3B), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0x66047857), RoundedCornerShape(12.dp))
                        .padding(8.dp)
                ) {
                    val logList = state.gameLog
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(logList) { logMsg ->
                            Text(
                                text = logMsg,
                                color = Color(0xFFA7F3D0),
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                // Next Hand Button
                Button(
                    onClick = { viewModel.startNewHand() },
                    enabled = !state.isHandInProgress && state.matchWinner == null,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(130.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEAB308),
                        disabledContainerColor = Color(0x44EAB308)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                        Text("NOVA MÃO", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }
                }
            }
        }

        // RULES MODAL
        if (state.showRulesModal) {
            AlertDialog(
                onDismissRequest = { viewModel.setRulesModalOpen(false) },
                title = { Text("Regras: Truco Soldado (PR)", color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("1. Cartas e Manilha: 4 < 5 < 6 < 7 < Q < J < K < 2 < 3. Manilha é a carta acima da Vira. Desempate por naipe: Ouros < Espadas < Copas < Paus.", color = Color.LightGray, fontSize = 11.sp)
                        Text("2. Pontos por Vaza: 1ª vaza vale 3 tentos, 2ª vale 2 tentos e 3ª vale 1 tento.", color = Color.LightGray, fontSize = 11.sp)
                        Text("3. Estouro e Vitória: 13+ tentos estoura e zera. Exatamente 12 tentos vence a partida!", color = Color.LightGray, fontSize = 11.sp)
                        Text("4. A Escura: Global ou Individual (11 tentos).", color = Color.LightGray, fontSize = 11.sp)
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

        // SLAP OVERLAY (TAPA NA VACA)
        if (state.showSlapOverlay) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("👋 TAPA NA VACA!", color = Color(0xFFEF4444), fontWeight = FontWeight.Black, fontSize = 22.sp) },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🐮", fontSize = 50.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${state.punishedPlayersOnEnd.joinToString(" e ")} terminou com 0 tentos!",
                            color = Color(0xFFFBBF24),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text("Castigo por terminar a partida zerado!", color = Color.LightGray, fontSize = 11.sp)
                    }
                },
                confirmButton = {
                    Button(onClick = { viewModel.dismissSlapOverlay() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))) {
                        Text("Continuar", fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = Color(0xFF180505)
            )
        }

        // VICTORY OVERLAY
        if (state.showVictoryOverlay) {
            val winnerName = state.matchWinner?.let { players[it].name } ?: ""
            AlertDialog(
                onDismissRequest = {},
                title = { Text("🏆 GRANDE VENCEDOR!", color = Color(0xFFFBBF24), fontWeight = FontWeight.Black, fontSize = 22.sp) },
                text = {
                    Column(verticalAlignment = Arrangement.spacedBy(8.dp)) {
                        Text("$winnerName cravou 12 tentos na partida!", color = Color(0xFFA7F3D0), fontWeight = FontWeight.Bold)
                        Divider(color = Color(0xFF064E3B))
                        Text("PLACAR FINAL:", color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        repeat(3) { i ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(players[i].name, color = Color.White, fontSize = 11.sp)
                                Text("${state.scores[i]} tentos (${state.resets[i]} estouros)", color = Color(0xFFFBBF24), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
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
