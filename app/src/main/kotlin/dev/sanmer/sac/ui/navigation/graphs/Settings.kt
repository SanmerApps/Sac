package dev.sanmer.sac.ui.navigation.graphs

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import dev.sanmer.sac.ui.animate.slideInLeftToRight
import dev.sanmer.sac.ui.animate.slideInRightToLeft
import dev.sanmer.sac.ui.animate.slideOutLeftToRight
import dev.sanmer.sac.ui.animate.slideOutRightToLeft
import dev.sanmer.sac.ui.navigation.MainScreen
import dev.sanmer.sac.ui.screens.settings.SettingsScreen
import dev.sanmer.sac.ui.screens.settings.about.AboutScreen

enum class SettingsScreen(val route: String) {
    Home("Settings"),
    About("About")
}

private val subScreens = listOf(
    SettingsScreen.About.route
)

fun NavGraphBuilder.settingsScreen(
    navController: NavController
) = navigation(
    startDestination = SettingsScreen.Home.route,
    route = MainScreen.Settings.route
) {
    composable(
        route = SettingsScreen.Home.route,
        enterTransition = {
            if (initialState.destination.route in subScreens) {
                slideInLeftToRight()
            } else {
                slideInRightToLeft()
            } + fadeIn()
        },
        exitTransition = {
            if (targetState.destination.route in subScreens) {
                slideOutRightToLeft()
            } else {
                slideOutLeftToRight()
            } + fadeOut()
        }
    ) {
        SettingsScreen(
            navController = navController
        )
    }

    composable(
        route = SettingsScreen.About.route,
        enterTransition = { slideInRightToLeft() + fadeIn() },
        exitTransition = { slideOutLeftToRight() + fadeOut() }
    ) {
        AboutScreen(
            navController = navController
        )
    }
}