package com.casino.pokervue.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.casino.pokervue.model.Card
import com.casino.pokervue.ui.theme.Cream
import com.casino.pokervue.ui.theme.Gold

@Composable
fun PlayingCard(
    card: Card?,
    width: Dp,
    height: Dp,
    cornerRadius: Dp,
    onClick: (() -> Unit)? = null
) {
    if (card == null) {
        CardSlot(width = width, height = height, cornerRadius = cornerRadius, onClick = onClick)
        return
    }

    val context = LocalContext.current
    val resId = remember(card) {
        context.resources.getIdentifier(
            "card_${card.rank.resName}_${card.suit.resName}",
            "drawable",
            context.packageName
        )
    }

    Box(
        modifier = Modifier
            .size(width, height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(Color.White)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        if (resId != 0) {
            Image(
                painter = painterResource(id = resId),
                contentDescription = "${card.rank.name} of ${card.suit.name}",
                modifier = Modifier.size(width, height),
                contentScale = ContentScale.Fit
            )
        } else {
            // Visible fallback if a specific card's resource wasn't found —
            // makes a naming mismatch obvious instead of silently blank
            Text("${card.rank.symbol}${card.suit.symbol}", modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun CardSlot(
    width: Dp,
    height: Dp,
    cornerRadius: Dp,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .size(width, height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(Color.Black.copy(alpha = 0.15f))
            .border(1.5.dp, Gold.copy(alpha = 0.38f), RoundedCornerShape(cornerRadius))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "+", color = Cream.copy(alpha = 0.5f), fontSize = 18.sp)
    }
}