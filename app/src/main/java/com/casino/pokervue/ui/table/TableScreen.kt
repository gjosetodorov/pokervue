package com.casino.pokervue.ui.table

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.casino.pokervue.R
import com.casino.pokervue.ui.components.CardPickerSheet
import com.casino.pokervue.ui.components.OpponentSheet
import com.casino.pokervue.ui.components.PlayingCard
import com.casino.pokervue.ui.theme.Cream
import com.casino.pokervue.ui.theme.Gold
import com.casino.pokervue.ui.theme.RajdhaniFamily
import kotlin.math.roundToInt

private sealed class ActiveSlot {
    data class Player(val index: Int) : ActiveSlot()
    data class Community(val index: Int) : ActiveSlot()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableScreen(
    gameViewModel: GameViewModel,
    onNavigateToDetails: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToCameraOrPicker: () -> Unit
) {
    val playerCards = gameViewModel.playerCards
    val communityCards = gameViewModel.communityCards
    val opponents = gameViewModel.opponents

    var activeSlot by remember { mutableStateOf<ActiveSlot?>(null) }
    var showOpponentSheet by remember { mutableStateOf(false) }

    val usedCards = remember(playerCards, communityCards) {
        (playerCards + communityCards).filterNotNull().toSet()
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val opponentSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.poker_table_background_red),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircleIconButton(onClick = onNavigateToSettings) {
                    Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = Cream)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CircleIconButton(onClick = { gameViewModel.reset() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Reset table", tint = Cream)
                    }
                    Box {
                        CircleIconButton(onClick = { showOpponentSheet = true }) {
                            Icon(Icons.Filled.Groups, contentDescription = "Opponents", tint = Cream)
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 4.dp, y = (-4).dp)
                                .size(18.dp)
                                .clip(RoundedCornerShape(9.dp))
                                .background(Gold)
                                .border(1.5.dp, Color(0xFF17181A), RoundedCornerShape(9.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = opponents.toString(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A1A),
                                textAlign = TextAlign.Center,
                                style = TextStyle(
                                    platformStyle = PlatformTextStyle(includeFontPadding = false)
                                )
                            )
                        }
                    }
                }
            }

            // Community cards
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                gameViewModel.currentHandName?.let { handName ->
                    Box(
                        modifier = Modifier
                            .padding(bottom = 12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.35f))
                            .border(1.dp, Gold.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = handName.uppercase(),
                            color = Gold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    communityCards.forEachIndexed { index, card ->
                        PlayingCard(
                            card = card,
                            width = 52.dp,
                            height = 78.dp,
                            cornerRadius = 6.dp,
                            highlighted = card != null && gameViewModel.highlightedCards.contains(card),
                            onClick = { activeSlot = ActiveSlot.Community(index) }
                        )
                    }
                }
            }

            // Player cards + percentage
            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = 114.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    playerCards.forEachIndexed { index, card ->
                        PlayingCard(
                            card = card,
                            width = 70.dp,
                            height = 106.dp,
                            cornerRadius = 9.dp,
                            highlighted = card != null && gameViewModel.highlightedCards.contains(card),
                            onClick = { activeSlot = ActiveSlot.Player(index) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    CircleIconButton(onClick = onNavigateToDetails, borderColor = Gold.copy(alpha = 0.5f)) {
                        Icon(Icons.Outlined.BarChart, contentDescription = "Details", tint = Gold)
                    }
                    Box(
                        modifier = Modifier.width(200.dp).height(76.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        when (val state = gameViewModel.equityState) {
                            is EquityState.Idle -> {
                                Text(
                                    text = "--",
                                    color = Color.White.copy(alpha = 0.3f),
                                    fontFamily = RajdhaniFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 76.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                            is EquityState.Calculating -> {
                                CircularProgressIndicator(
                                    color = Gold,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            is EquityState.Result -> {
                                Text(
                                    text = "${state.winPercent.roundToInt()}%",
                                    color = Gold,
                                    fontFamily = RajdhaniFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 76.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    CircleIconButton(onClick = onNavigateToCameraOrPicker, borderColor = Gold.copy(alpha = 0.5f)) {
                        Icon(Icons.Filled.CameraAlt, contentDescription = "Scan cards", tint = Gold)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("WIN EQUITY", color = Gold, fontSize = 9.5.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 2.sp)
            }
        }
    }

    // Card picker bottom sheet
    if (activeSlot != null) {
        ModalBottomSheet(
            onDismissRequest = { activeSlot = null },
            sheetState = sheetState,
            containerColor = Color.Transparent,
            dragHandle = null
        ) {
            CardPickerSheet(
                usedCards = usedCards,
                onSelect = { selectedCard ->
                    when (val slot = activeSlot) {
                        is ActiveSlot.Player -> gameViewModel.setPlayerCard(slot.index, selectedCard)
                        is ActiveSlot.Community -> gameViewModel.setCommunityCard(slot.index, selectedCard)
                        null -> {}
                    }
                },
                onDismiss = { activeSlot = null }
            )
        }
    }

    // Opponent count bottom sheet
    if (showOpponentSheet) {
        ModalBottomSheet(
            onDismissRequest = { showOpponentSheet = false },
            sheetState = opponentSheetState,
            containerColor = Color.Transparent,
            dragHandle = null
        ) {
            OpponentSheet(
                currentValue = opponents,
                onValueSelected = { gameViewModel.updateOpponents(it) },
                onDismiss = { showOpponentSheet = false }
            )
        }
    }
}

@Composable
private fun CircleIconButton(
    onClick: () -> Unit,
    borderColor: Color = Gold.copy(alpha = 0.45f),
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.size(42.dp).clip(RoundedCornerShape(21.dp)).background(Color.Black.copy(alpha = 0.22f)),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick) { content() }
    }
}