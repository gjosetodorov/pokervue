package com.casino.pokervue.navigation

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.casino.pokervue.ui.details.DetailsScreen
import com.casino.pokervue.ui.settings.SettingsScreen
import com.casino.pokervue.ui.table.GameViewModel
import com.casino.pokervue.ui.table.TableScreen

object Routes {
    const val TABLE = "table"
    const val DETAILS = "details"
    const val SETTINGS = "settings"
}

@Composable
fun PokerVueNavGraph() {
    val navController: NavHostController = rememberNavController()
    val gameViewModel: GameViewModel = viewModel()

    Surface(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = Routes.TABLE) {
            composable(Routes.TABLE) {
                TableScreen(
                    gameViewModel = gameViewModel,
                    onNavigateToDetails = { navController.navigate(Routes.DETAILS) },
                    onNavigateToSettings = { navController.navigate(Routes.SETTINGS) },
                    onNavigateToCameraOrPicker = { /* wired up later */ }
                )
            }
            composable(Routes.DETAILS) {
                DetailsScreen(
                    playerCards = gameViewModel.playerCards,
                    communityCards = gameViewModel.communityCards,
                    equityState = gameViewModel.equityState,
                    opponents = gameViewModel.opponents,
                    outs = gameViewModel.outs,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}