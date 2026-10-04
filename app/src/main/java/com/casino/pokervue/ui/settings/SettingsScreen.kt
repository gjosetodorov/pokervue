package com.casino.pokervue.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.casino.pokervue.ui.components.OpponentSheet
import com.casino.pokervue.ui.components.SimulationAccuracySheet
import com.casino.pokervue.ui.theme.AppThemeState
import com.casino.pokervue.ui.theme.DangerRed
import com.casino.pokervue.ui.theme.FeltRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val darkMode by viewModel.darkMode.collectAsState()
    val defaultOpponents by viewModel.defaultOpponents.collectAsState()
    val hapticFeedback by viewModel.hapticFeedback.collectAsState()
    val simulationIterations by viewModel.simulationIterations.collectAsState()
    var showResetConfirm by remember { mutableStateOf(false) }
    var showOpponentPicker by remember { mutableStateOf(false) }
    var showAccuracyPicker by remember { mutableStateOf(false) }

    val haptic = LocalHapticFeedback.current
    val background = MaterialTheme.colorScheme.background
    val onBackground = MaterialTheme.colorScheme.onBackground
    val dividerColor = onBackground.copy(alpha = 0.08f)
    val mutedText = onBackground.copy(alpha = 0.5f)

    LaunchedEffect(darkMode) {
        AppThemeState.isDarkTheme = darkMode
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 32.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.clip(RoundedCornerShape(21.dp)).background(onBackground.copy(alpha = 0.08f))) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = onBackground)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Settings", color = onBackground, fontWeight = FontWeight.Bold, fontSize = 19.sp)
        }
        HorizontalDivider(color = dividerColor)

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            SectionLabel("Appearance", mutedText)
            SettingsRow(
                icon = Icons.Outlined.Palette,
                label = "Dark Mode",
                toggle = darkMode,
                onToggle = { viewModel.setDarkMode(it) },
                textColor = onBackground,
                mutedColor = mutedText
            )

            SectionLabel("Gameplay", mutedText)
            SettingsRow(
                icon = Icons.Filled.Groups,
                label = "Default Opponents",
                value = defaultOpponents.toString(),
                onClick = { showOpponentPicker = true },
                textColor = onBackground,
                mutedColor = mutedText
            )
            SettingsRow(
                icon = Icons.Filled.Speed,
                label = "Simulation Accuracy",
                value = simulationAccuracyLabel(simulationIterations),
                onClick = { showAccuracyPicker = true },
                textColor = onBackground,
                mutedColor = mutedText
            )

            SectionLabel("General", mutedText)
            SettingsRow(
                icon = Icons.Filled.Vibration,
                label = "Haptic Feedback",
                toggle = hapticFeedback,
                onToggle = { enabled ->
                    viewModel.setHapticFeedback(enabled)
                    if (enabled) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                },
                textColor = onBackground,
                mutedColor = mutedText
            )

            SectionLabel("Help", mutedText)
            SettingsRow(icon = Icons.Filled.HelpOutline, label = "How the App Works", onClick = { /* TODO */ }, textColor = onBackground, mutedColor = mutedText)
            SettingsRow(icon = Icons.Filled.Info, label = "About This App", onClick = { /* TODO */ }, textColor = onBackground, mutedColor = mutedText)

            SectionLabel("Data", mutedText)
            SettingsRow(
                icon = Icons.Filled.Delete,
                label = "Reset All Settings",
                danger = true,
                onClick = { showResetConfirm = true },
                textColor = onBackground,
                mutedColor = mutedText
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Version 1.0.0",
                fontSize = 12.sp,
                color = mutedText,
                modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Reset all settings?") },
            text = { Text("This restores every setting to its default value. This can't be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetAll()
                    showResetConfirm = false
                }) { Text("Reset", color = DangerRed) }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) { Text("Cancel") }
            }
        )
    }

    if (showOpponentPicker) {
        ModalBottomSheet(
            onDismissRequest = { showOpponentPicker = false },
            containerColor = Color.Transparent,
            dragHandle = null
        ) {
            OpponentSheet(
                currentValue = defaultOpponents,
                hapticsEnabled = hapticFeedback,
                onValueSelected = { viewModel.setDefaultOpponents(it) },
                onDismiss = { showOpponentPicker = false }
            )
        }
    }

    if (showAccuracyPicker) {
        ModalBottomSheet(
            onDismissRequest = { showAccuracyPicker = false },
            containerColor = Color.Transparent,
            dragHandle = null
        ) {
            SimulationAccuracySheet(
                currentValue = simulationIterations,
                onValueSelected = { viewModel.setSimulationIterations(it) },
                onDismiss = { showAccuracyPicker = false }
            )
        }
    }
}

private fun simulationAccuracyLabel(iterations: Int): String = when (iterations) {
    1000 -> "Fast"
    3000 -> "Balanced"
    5000 -> "Precise"
    else -> "Balanced"
}

@Composable
private fun SectionLabel(text: String, color: Color) {
    Text(
        text = text,
        fontSize = 13.5.sp,
        fontWeight = FontWeight.Medium,
        color = color,
        modifier = Modifier.padding(top = 24.dp, bottom = 6.dp)
    )
}

@Composable
private fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    textColor: Color,
    mutedColor: Color,
    value: String? = null,
    toggle: Boolean? = null,
    onToggle: ((Boolean) -> Unit)? = null,
    danger: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (danger) DangerRed else textColor.copy(alpha = 0.85f),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            fontSize = 15.sp,
            color = if (danger) DangerRed else textColor,
            modifier = Modifier.weight(1f)
        )
        when {
            toggle != null -> Switch(
                checked = toggle,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(checkedTrackColor = FeltRed)
            )
            value != null -> Text(value, fontSize = 13.5.sp, color = mutedColor)
        }
    }
    HorizontalDivider(color = mutedColor.copy(alpha = 0.15f))
}