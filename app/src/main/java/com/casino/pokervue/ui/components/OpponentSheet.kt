package com.casino.pokervue.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.casino.pokervue.ui.theme.Gold
import com.casino.pokervue.ui.theme.RajdhaniFamily
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpponentSheet(
    currentValue: Int,
    hapticsEnabled: Boolean,
    onValueSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var tempValue by remember(currentValue) { mutableFloatStateOf(currentValue.toFloat()) }
    val haptic = LocalHapticFeedback.current

    val background = MaterialTheme.colorScheme.background
    val onBackground = MaterialTheme.colorScheme.onBackground

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(background)
            .padding(horizontal = 24.dp)
            .padding(top = 12.dp, bottom = 36.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(width = 40.dp, height = 4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(onBackground.copy(alpha = 0.2f))
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Number of opponents",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = onBackground.copy(alpha = 0.5f),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = tempValue.roundToInt().toString(),
            fontFamily = RajdhaniFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp,
            color = onBackground,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(28.dp))

        Slider(
            value = tempValue,
            onValueChange = { newValue ->
                val rounded = newValue.roundToInt().toFloat()
                if (rounded != tempValue && hapticsEnabled) {
                    haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                }
                tempValue = rounded
            },
            onValueChangeFinished = {
                tempValue = tempValue.roundToInt().coerceIn(1, 8).toFloat()
                onValueSelected(tempValue.roundToInt())
            },
            valueRange = 1f..8f,
            steps = 6,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = onBackground
            ),
            thumb = {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(onBackground)
                )
            },
            track = { sliderState ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(onBackground.copy(alpha = 0.1f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(
                                fraction = sliderState.valueRange.let {
                                    (sliderState.value - it.start) /
                                            (it.endInclusive - it.start)
                                }
                            )
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(2.dp))
                            .background(Gold)
                    )
                }
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("1", fontSize = 11.sp, color = onBackground.copy(alpha = 0.35f))
            Text("8", fontSize = 11.sp, color = onBackground.copy(alpha = 0.35f))
        }
    }
}