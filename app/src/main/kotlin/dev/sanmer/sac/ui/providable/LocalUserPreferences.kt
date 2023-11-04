package dev.sanmer.sac.ui.providable

import androidx.compose.runtime.staticCompositionLocalOf
import dev.sanmer.sac.datastore.UserPreferencesExt

val LocalUserPreferences = staticCompositionLocalOf { UserPreferencesExt.default() }
