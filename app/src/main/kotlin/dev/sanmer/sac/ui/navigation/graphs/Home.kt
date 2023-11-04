package dev.sanmer.sac.ui.navigation.graphs

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import dev.sanmer.sac.ui.navigation.MainScreen
import dev.sanmer.sac.ui.screens.home.HomeScreen

enum class HomeScreen(val route: String) {
    Home("Home")
}

fun NavGraphBuilder.homeScreen(
    navController: NavController
) = navigation(
    startDestination = HomeScreen.Home.route,
    route = MainScreen.Home.route
) {
    composable(
        route = HomeScreen.Home.route,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        HomeScreen(
            navController = navController
        )
    }
}