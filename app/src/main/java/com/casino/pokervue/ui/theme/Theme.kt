package com.casino.pokervue.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

object AppThemeState {
    var isDarkTheme by mutableStateOf(true)
}

private val PokerVueDarkScheme = darkColorScheme(
    primary = Gold,
    onPrimary = DarkSurface,
    secondary = FeltRed,
    onSecondary = Cream,
    background = DarkSurface,
    onBackground = Cream,
    surface = DarkSurface,
    onSurface = Cream,
    error = DangerRed,
    onError = Cream
)

private val PokerVueLightScheme = lightColorScheme(
    primary = GoldOnLight,
    onPrimary = LightSurface,
    secondary = FeltRed,
    onSecondary = LightSurface,
    background = LightSurface,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    error = DangerRed,
    onError = LightSurface
)

@Composable
fun PokerVueTheme(
    darkTheme: Boolean = AppThemeState.isDarkTheme,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) PokerVueDarkScheme else PokerVueLightScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}