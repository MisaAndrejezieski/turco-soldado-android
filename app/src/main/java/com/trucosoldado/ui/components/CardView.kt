package com.trucosoldado.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trucosoldado.model.Card

@Composable
fun CardItemView(
    card: Card?,
    isManilha: Boolean = false,
    isBack: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = Modifier
            .width(60.dp)
            .height(84.dp)
            .clip(shape)
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            )
    ) {
        if (isBack || card == null) {
            // Card Back (Pig motif)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFF472B6), Color(0xFFDB2777))
                        )
                    )
                    .border(2.dp, Color(0xFFFBCFE8), shape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🐷", fontSize = 24.sp)
            }
        } else {
            // Card Front
            val suitColor = if (card.suit.colorHex == "#DC2626") Color(0xFFDC2626) else Color(0xFF0F172A)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8FAFC))
                    .border(
                        width = if (isManilha) 3.dp else 2.dp,
                        color = if (isManilha) Color(0xFFFBBF24) else Color(0xFFCBD5E1),
                        shape = shape
                    )
                    .padding(6.dp)
            ) {
                Text(
                    text = card.rank,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = suitColor,
                    modifier = Modifier.align(Alignment.TopStart)
                )
                Text(
                    text = card.suit.symbol,
                    fontSize = 24.sp,
                    color = suitColor,
                    modifier = Modifier.align(Alignment.Center)
                )
                Text(
                    text = card.rank,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = suitColor,
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }
        }
    }
}
