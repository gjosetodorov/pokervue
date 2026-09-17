package com.casino.pokervue.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.casino.pokervue.ui.theme.Cream
import com.casino.pokervue.ui.theme.DarkSurface
import com.casino.pokervue.ui.theme.Gold
import com.casino.pokervue.ui.theme.RajdhaniFamily
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpponentSheet(
    currentValue: Int,
    onValueSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var tempValue by remember(currentValue) { mutableFloatStateOf(currentValue.toFloat()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(DarkSurface)
            .padding(horizontal = 24.dp)
            .padding(top = 12.dp, bottom = 36.dp)
    ) {
        // Drag handle
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(width = 40.dp, height = 4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White.copy(alpha = 0.2f))
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Number of opponents",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Cream.copy(alpha = 0.5f),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = tempValue.roundToInt().toString(),
            fontFamily = RajdhaniFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp,
            color = Cream,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(28.dp))

        // Continuous slider (no tick dots) — snapped to whole numbers ourselves,
        // with a custom circular thumb matching the Figma design.
        Slider(
            value = tempValue,
            onValueChange = { tempValue = it },
            onValueChangeFinished = {
                tempValue = tempValue.roundToInt().toFloat()
                onValueSelected(tempValue.roundToInt())
            },
            valueRange = 1f..8f,
            colors = SliderDefaults.colors(
                activeTrackColor = Gold,
                inactiveTrackColor = Color.White.copy(alpha = 0.1f)
            ),
            thumb = {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Cream)
                )
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("1", fontSize = 11.sp, color = Cream.copy(alpha = 0.35f))
            Text("8", fontSize = 11.sp, color = Cream.copy(alpha = 0.35f))
        }
    }
}