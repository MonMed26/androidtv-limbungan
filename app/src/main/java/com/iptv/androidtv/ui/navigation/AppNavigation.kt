package com.iptv.androidtv.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.iptv.androidtv.ui.home.HomeScreen
import com.iptv.androidtv.ui.player.PlayerScreen
import com.iptv.androidtv.ui.search.SearchScreen
import com.iptv.androidtv.ui.settings.SettingsScreen
import com.iptv.androidtv.ui.setup.SetupScreen

object Routes {
    const val SETUP = "setup"
    const val HOME = "home"
    const val PLAYER = "player/{channelId}"
    const val SETTINGS = "settings"
    const val SEARCH = "search"

    fun player(channelId: Long) = "player/$channelId"
}

@Composable
fun AppNavigation(startDestination: String) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.SETUP) {
            SetupScreen(
                onSetupComplete = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SETUP) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onChannelClick = { channel ->
                    navController.navigate(Routes.player(channel.id))
                },
                onSearchClick = {
                    navController.navigate(Routes.SEARCH)
                },
                onSettingsClick = {
                    navController.navigate(Routes.SETTINGS)
                }
            )
        }

        composable(
            route = Routes.PLAYER,
            arguments = listOf(
                navArgument("channelId") { type = NavType.LongType }
            )
        ) {
            PlayerScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SEARCH) {
            SearchScreen(
                onChannelClick = { channel ->
                    navController.navigate(Routes.player(channel.id))
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
