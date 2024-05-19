package dev.sanmer.sac.datastore

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import dev.sanmer.sac.compat.BuildCompat
import dev.sanmer.sac.io.Endian
import dev.sanmer.sac.ui.theme.Colors

data class UserPreferencesCompat(
    val darkMode: DarkMode,
    val themeColor: Int,
    val endian: Endian
) {
    constructor(original: UserPreferences) : this(
        darkMode = original.darkMode,
        themeColor = original.themeColor,
        endian = Endian.valueOf(original.endian)
    )

    @Composable
    fun isDarkMode() = when (darkMode) {
        DarkMode.ALWAYS_OFF -> false
        DarkMode.ALWAYS_ON -> true
        else -> isSystemInDarkTheme()
    }

    fun toProto(): UserPreferences = UserPreferences.newBuilder()
        .setDarkMode(darkMode)
        .setThemeColor(themeColor)
        .setEndian(endian.name)
        .build()

    companion object {
        fun default() = UserPreferencesCompat(
            darkMode = DarkMode.FOLLOW_SYSTEM,
            themeColor = if (BuildCompat.atLeastS) Colors.Dynamic.id else Colors.Pourville.id,
            endian = Endian.Little
        )

        fun UserPreferences.new(
            block: UserPreferencesKt.Dsl.() -> Unit
        ) = UserPreferencesCompat(this)
            .toProto()
            .copy(block)
    }
}