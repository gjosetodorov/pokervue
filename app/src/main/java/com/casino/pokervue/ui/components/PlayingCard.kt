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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.casino.pokervue.model.Card
import com.casino.pokervue.ui.theme.Cream
import com.casino.pokervue.ui.theme.Gold
import android.graphics.Paint as AndroidPaint

private val HighlightYellow = Color(0xFFFFD54F)

@Composable
fun PlayingCard(
    card: Card?,
    width: Dp,
    height: Dp,
    cornerRadius: Dp,
    highlighted: Boolean = false,
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
            .then(
                if (highlighted) {
                    Modifier.drawBehind {
                        val paint = AndroidPaint().apply {
                            color = HighlightYellow.copy(alpha = 0.85f).toArgb()
                            maskFilter = android.graphics.BlurMaskFilter(
                                24f, // blur radius in pixels — controls glow softness
                                android.graphics.BlurMaskFilter.Blur.NORMAL
                            )
                        }
                        drawIntoCanvas { canvas ->
                            canvas.nativeCanvas.drawRoundRect(
                                0f, 0f, size.width, size.height,
                                cornerRadius.toPx(), cornerRadius.toPx(),
                                paint
                            )
                        }
                    }
                } else {
                    Modifier.shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(cornerRadius),
                        ambientColor = Color.Black.copy(alpha = 0.5f),
                        spotColor = Color.Black.copy(alpha = 0.5f)
                    )
                }
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(Color.White)
            .then(
                if (highlighted) {
                    Modifier.border(1.5.dp, HighlightYellow, RoundedCornerShape(cornerRadius))
                } else Modifier
            )
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