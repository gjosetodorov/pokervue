package com.casino.pokervue.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.casino.pokervue.ui.theme.Gold

private data class AccuracyOption(val iterations: Int, val label: String, val description: String)

private val accuracyOptions = listOf(
    AccuracyOption(1000, "Fast", "Quicker results, slightly less precise. Good for fast-paced play."),
    AccuracyOption(3000, "Balanced", "A solid mix of speed and accuracy. Recommended for most players."),
    AccuracyOption(5000, "Precise", "The most accurate results. May calculate a little slower.")
)

@Composable
fun SimulationAccuracySheet(
    currentValue: Int,
    onValueSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val background = MaterialTheme.colorScheme.background
    val onBackground = MaterialTheme.colorScheme.onBackground

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(background)
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 32.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(width = 40.dp, height = 4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(onBackground.copy(alpha = 0.2f))
        )
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Simulation Accuracy",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = onBackground,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(20.dp))

        accuracyOptions.forEach { option ->
            val selected = option.iterations == currentValue
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (selected) Gold.copy(alpha = 0.12f) else Color.Transparent)
                    .clickable {
                        onValueSelected(option.iterations)
                        onDismiss()
                    }
                    .padding(horizontal = 14.dp, vertical = 14.dp)
            ) {
                Text(
                    text = option.label,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (selected) Gold else onBackground
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = option.description,
                    fontSize = 12.5.sp,
                    color = onBackground.copy(alpha = 0.5f)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}