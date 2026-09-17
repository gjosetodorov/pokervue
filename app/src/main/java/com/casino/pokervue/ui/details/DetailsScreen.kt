package com.casino.pokervue.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.casino.pokervue.model.Card
import com.casino.pokervue.model.Rank
import com.casino.pokervue.model.Suit
import com.casino.pokervue.ui.components.PlayingCard
import com.casino.pokervue.ui.table.EquityState
import com.casino.pokervue.ui.theme.Cream
import com.casino.pokervue.ui.theme.DarkSurface
import com.casino.pokervue.ui.theme.Gold
import com.casino.pokervue.ui.theme.RajdhaniFamily
import kotlin.math.roundToInt

private data class HandProbRow(val name: String, val probability: String, val active: Boolean = false)

@Composable
fun DetailsScreen(
    playerCards: List<Card?>,
    communityCards: List<Card?>,
    equityState: EquityState,
    opponents: Int,
    onNavigateBack: () -> Unit
) {
    val handProbs = listOf(
        HandProbRow("Royal Flush", "< 0.1%"),
        HandProbRow("Straight Flush", "0.2%"),
        HandProbRow("Four of a Kind", "1.4%"),
        HandProbRow("Full House", "12.8%"),
        HandProbRow("Flush", "28.5%", active = true),
        HandProbRow("Straight", "4.2%"),
        HandProbRow("Three of a Kind", "8.1%"),
        HandProbRow("Two Pair", "22.0%"),
        HandProbRow("One Pair", "18.3%"),
        HandProbRow("High Card", "4.5%")
    )

    val (winPct, tiePct, losePct) = when (equityState) {
        is EquityState.Result -> Triple(equityState.winPercent, equityState.tiePercent, equityState.losePercent)
        else -> Triple(0.0, 0.0, 0.0)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSurface)
            .verticalScroll(rememberScrollState())
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 32.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(21.dp))
                    .background(Color.Black.copy(alpha = 0.22f))
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Cream)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Hand Analysis", color = Cream, fontWeight = FontWeight.Bold, fontSize = 19.sp)
        }
        HorizontalDivider(color = Color.White.copy(alpha = 0.07f))

        // Cards section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Player's hole cards — on top, larger, primary focus
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                playerCards.forEach { card ->
                    PlayingCard(card = card, width = 64.dp, height = 96.dp, cornerRadius = 9.dp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Community cards — all 5 in a single row, slightly bigger than before
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                communityCards.forEach { card ->
                    PlayingCard(card = card, width = 46.dp, height = 70.dp, cornerRadius = 6.dp)
                }
            }
        }

        // Win percentage
        Column(
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.height(68.dp), contentAlignment = Alignment.Center) {
                when (equityState) {
                    is EquityState.Idle -> {
                        Text(
                            text = "--",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 68.sp,
                            color = Color.White.copy(alpha = 0.3f)
                        )
                    }
                    is EquityState.Calculating -> {
                        CircularProgressIndicator(
                            color = Gold,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    is EquityState.Result -> {
                        Text(
                            text = "${equityState.winPercent.roundToInt()}%",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 68.sp,
                            color = Color.White
                        )
                    }
                }
            }
            Text(
                text = "WIN EQUITY",
                color = Gold,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp
            )
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            // Outcome breakdown
            SectionLabel("Outcome Breakdown")
            OutcomeRow("Win", "${winPct.roundToInt()}%", highlight = true)
            OutcomeRow("Tie", "${tiePct.roundToInt()}%")
            OutcomeRow("Lose", "${losePct.roundToInt()}%")
            HorizontalDivider(color = Color.White.copy(alpha = 0.07f))
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White.copy(alpha = 0.08f))
            ) {
                Box(modifier = Modifier.weight(winPct.toFloat().coerceAtLeast(0.01f)).fillMaxHeight().background(Gold))
                Box(modifier = Modifier.weight(tiePct.toFloat().coerceAtLeast(0.01f)).fillMaxHeight().background(Cream))
                Box(modifier = Modifier.weight(losePct.toFloat().coerceAtLeast(0.01f)).fillMaxHeight().background(Color(0xFF7A1F2B)))
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Hand probabilities
            SectionLabel("Hand Probabilities")
            handProbs.forEachIndexed { index, row ->
                OutcomeRow(row.name, row.probability, highlight = row.active)
                if (index < handProbs.lastIndex) {
                    HorizontalDivider(color = Color.White.copy(alpha = 0.07f))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Improving outs
            SectionLabel("Improving Outs")
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "8 Outs",
                        fontFamily = RajdhaniFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Cream
                    )
                    Text(
                        text = "Cards that improve your hand",
                        fontSize = 12.sp,
                        color = Cream.copy(alpha = 0.4f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    PlayingCard(card = Card(Rank.QUEEN, Suit.SPADES), width = 32.dp, height = 48.dp, cornerRadius = 4.dp)
                    PlayingCard(card = Card(Rank.QUEEN, Suit.HEARTS), width = 32.dp, height = 48.dp, cornerRadius = 4.dp)
                }
            }
            HorizontalDivider(color = Color.White.copy(alpha = 0.07f))

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = if (opponents == 1) {
                    "Calculated against 1 random opponent hand."
                } else {
                    "Calculated against $opponents random opponent hands."
                },
                fontSize = 12.sp,
                color = Cream.copy(alpha = 0.35f),
                modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.5.sp,
        fontWeight = FontWeight.Medium,
        color = Cream.copy(alpha = 0.5f),
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
private fun OutcomeRow(label: String, value: String, highlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.5.sp,
            color = if (highlight) Gold else Cream.copy(alpha = 0.75f)
        )
        Text(
            text = value,
            fontFamily = RajdhaniFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = if (highlight) Gold else Cream
        )
    }
}