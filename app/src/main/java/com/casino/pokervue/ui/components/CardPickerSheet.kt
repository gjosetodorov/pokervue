package com.casino.pokervue.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.casino.pokervue.R
import com.casino.pokervue.model.Card
import com.casino.pokervue.model.Rank
import com.casino.pokervue.model.Suit
import com.casino.pokervue.ui.theme.DarkSurface

@Composable
fun CardPickerSheet(
    usedCards: Set<Card>,
    onSelect: (Card) -> Unit,
    onDismiss: () -> Unit
) {
    var activeSuit by remember { mutableIntStateOf(0) }
    val suits = Suit.entries
    val suit = suits[activeSuit]
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(DarkSurface)
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 32.dp)
    ) {
        // Drag handle
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(width = 48.dp, height = 6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color.White.copy(alpha = 0.2f))
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Suit tabs — segmented selector
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            suits.forEachIndexed { i, s ->
                val active = i == activeSuit
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (active) Color.White else Color.White.copy(alpha = 0.05f))
                        .clickable { activeSuit = i },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = suitIconRes(s)),
                        contentDescription = s.name,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        // Rank grid — 4 columns, real card PNGs
        val ranks = Rank.entries.reversed() // Ace first
        val rows = ranks.chunked(4)

        rows.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { rank ->
                    val card = Card(rank, suit)
                    val disabled = usedCards.contains(card)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(2f / 3f)
                            .clip(RoundedCornerShape(9.dp))
                            .background(if (disabled) Color.White.copy(alpha = 0.04f) else Color.White)
                            .then(
                                if (!disabled) Modifier.clickable {
                                    onSelect(card)
                                    onDismiss()
                                } else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!disabled) {
                            val resId = remember(card) {
                                context.resources.getIdentifier(
                                    "card_${card.rank.resName}_${card.suit.resName}",
                                    "drawable",
                                    context.packageName
                                )
                            }
                            if (resId != 0) {
                                Image(
                                    painter = painterResource(id = resId),
                                    contentDescription = "${rank.name} of ${suit.name}",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                    }
                }
                // pad incomplete rows (the "2" row only has 1 card) so it aligns left, not stretched
                repeat(4 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

private fun suitIconRes(suit: Suit): Int = when (suit) {
    Suit.SPADES -> R.drawable.suit_spade
    Suit.HEARTS -> R.drawable.suit_heart
    Suit.DIAMONDS -> R.drawable.suit_diamond
    Suit.CLUBS -> R.drawable.suit_club
}