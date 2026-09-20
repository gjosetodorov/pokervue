package com.casino.pokervue.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.casino.pokervue.logic.HandEvaluator
import com.casino.pokervue.logic.PartialHandDetector
import com.casino.pokervue.model.Card
import com.casino.pokervue.model.HandCategory
import com.casino.pokervue.ui.components.PlayingCard
import com.casino.pokervue.ui.table.EquityState
import com.casino.pokervue.ui.theme.Cream
import com.casino.pokervue.ui.theme.DarkSurface
import com.casino.pokervue.ui.theme.Gold
import com.casino.pokervue.ui.theme.RajdhaniFamily
import kotlin.math.roundToInt

@Composable
fun DetailsScreen(
    playerCards: List<Card?>,
    communityCards: List<Card?>,
    equityState: EquityState,
    opponents: Int,
    outs: List<Card>,
    onNavigateBack: () -> Unit
) {
    var showAllOuts by remember { mutableStateOf(false) }

    val winPct: Double
    val tiePct: Double
    val losePct: Double
    val categoryPercents: Map<HandCategory, Double>
    when (equityState) {
        is EquityState.Result -> {
            winPct = equityState.winPercent
            tiePct = equityState.tiePercent
            losePct = equityState.losePercent
            categoryPercents = equityState.handCategoryPercents
        }
        else -> {
            winPct = 0.0
            tiePct = 0.0
            losePct = 0.0
            categoryPercents = emptyMap()
        }
    }

    val knownPlayer = playerCards.filterNotNull()
    val knownBoard = communityCards.filterNotNull()

    val currentCategory = if (knownPlayer.size == 2) {
        val allCards = knownPlayer + knownBoard
        if (allCards.size >= 5) {
            HandEvaluator.evaluateBestHand(allCards).rank.category
        } else {
            when (PartialHandDetector.detect(allCards)?.name) {
                "Pair" -> HandCategory.PAIR
                "Two Pair" -> HandCategory.TWO_PAIR
                "Three of a Kind" -> HandCategory.THREE_OF_A_KIND
                else -> HandCategory.HIGH_CARD
            }
        }
    } else null

    val displayOrder = listOf(
        HandCategory.STRAIGHT_FLUSH to "Straight Flush",
        HandCategory.FOUR_OF_A_KIND to "Four of a Kind",
        HandCategory.FULL_HOUSE to "Full House",
        HandCategory.FLUSH to "Flush",
        HandCategory.STRAIGHT to "Straight",
        HandCategory.THREE_OF_A_KIND to "Three of a Kind",
        HandCategory.TWO_PAIR to "Two Pair",
        HandCategory.PAIR to "Pair",
        HandCategory.HIGH_CARD to "High Card"
    )

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
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                playerCards.forEach { card ->
                    PlayingCard(card = card, width = 64.dp, height = 96.dp, cornerRadius = 9.dp)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
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

            // Hand probabilities — real, from simulation, current hand highlighted
            SectionLabel("Hand Probabilities")
            displayOrder.forEachIndexed { index, (category, label) ->
                val percent = categoryPercents[category] ?: 0.0
                val valueText = if (percent in 0.0..0.05) "0%" else if (percent < 0.1) "< 0.1%" else "${percent.roundToInt()}%"
                OutcomeRow(
                    label = label,
                    value = valueText,
                    highlight = category == currentCategory
                )
                if (index < displayOrder.lastIndex) {
                    HorizontalDivider(color = Color.White.copy(alpha = 0.07f))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Improving outs
            SectionLabel("Improving Outs")
            val boardIsFull = knownBoard.size >= 5
            val boardTooEarly = knownBoard.size < 3
            when {
                boardTooEarly -> {
                    Text(
                        text = "Add the flop to see improving outs.",
                        fontSize = 13.sp,
                        color = Cream.copy(alpha = 0.4f),
                        modifier = Modifier.padding(vertical = 14.dp)
                    )
                }
                boardIsFull -> {
                    Text(
                        text = "No more cards to come.",
                        fontSize = 13.sp,
                        color = Cream.copy(alpha = 0.4f),
                        modifier = Modifier.padding(vertical = 14.dp)
                    )
                }
                else -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp)
                            .then(if (outs.isNotEmpty()) Modifier.clickable { showAllOuts = true } else Modifier),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${outs.size} Outs",
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
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            outs.take(4).forEach { card ->
                                PlayingCard(card = card, width = 32.dp, height = 48.dp, cornerRadius = 4.dp)
                            }
                            val remaining = outs.size - 4
                            if (remaining > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp, 48.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.White.copy(alpha = 0.08f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("+$remaining", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Cream)
                                }
                            }
                        }
                    }
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

    if (showAllOuts) {
        AlertDialog(
            onDismissRequest = { showAllOuts = false },
            title = { Text("Cards that improve your hand") },
            text = { OutsGrid(outs) },
            confirmButton = {
                TextButton(onClick = { showAllOuts = false }) { Text("Close") }
            }
        )
    }
}

@Composable
private fun OutsGrid(outs: List<Card>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        outs.chunked(6).forEach { rowCards ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                rowCards.forEach { card ->
                    PlayingCard(card = card, width = 36.dp, height = 54.dp, cornerRadius = 4.dp)
                }
            }
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