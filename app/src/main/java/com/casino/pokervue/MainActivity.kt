package com.casino.pokervue

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import com.casino.pokervue.navigation.PokerVueNavGraph
import com.casino.pokervue.ui.theme.AppThemeState
import com.casino.pokervue.ui.theme.PokerVueTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokerVueTheme(darkTheme = AppThemeState.isDarkTheme) {
                PokerVueNavGraph()
            }
        }
    }
}