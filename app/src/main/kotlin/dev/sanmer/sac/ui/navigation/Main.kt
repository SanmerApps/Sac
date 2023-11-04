package dev.sanmer.sac.ui.navigation

import androidx.navigation.NavController
import dev.sanmer.sac.ui.utils.navigatePopUpTo

enum class MainScreen(val route: String) {
    Home("HomeScreen"),
    Settings("SettingsScreen")
}

fun NavController.navigateToSettings() = navigatePopUpTo(MainScreen.Settings.route)