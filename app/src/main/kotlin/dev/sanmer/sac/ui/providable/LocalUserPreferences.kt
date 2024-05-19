package dev.sanmer.sac.ui.providable

import androidx.compose.runtime.staticCompositionLocalOf
import dev.sanmer.sac.datastore.UserPreferencesCompat

val LocalUserPreferences = staticCompositionLocalOf { UserPreferencesCompat.default() }
