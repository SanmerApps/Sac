package dev.sanmer.sac.datastore

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import dev.sanmer.sac.app.utils.OsUtils
import dev.sanmer.sac.io.Endian
import dev.sanmer.sac.ui.theme.Colors

data class UserPreferencesExt(
    val darkMode: DarkMode,
    val themeColor: Int,
    val endian: Endian
) {
    companion object {
        fun default() = UserPreferencesExt(
            darkMode = DarkMode.FOLLOW_SYSTEM,
            themeColor = if (OsUtils.atLeastS) Colors.Dynamic.id else Colors.Pourville.id,
            endian = Endian.Little
        )
    }
}

@Composable
fun UserPreferencesExt.isDarkMode() = when (darkMode) {
    DarkMode.ALWAYS_OFF -> false
    DarkMode.ALWAYS_ON -> true
    else -> isSystemInDarkTheme()
}

fun UserPreferencesExt.toProto(): UserPreferences = UserPreferences.newBuilder()
    .setDarkMode(darkMode)
    .setThemeColor(themeColor)
    .setEndian(endian.name)
    .build()

fun UserPreferences.toExt() = UserPreferencesExt(
    darkMode = darkMode,
    themeColor = themeColor,
    endian = Endian.valueOf(endian)
)

fun UserPreferences.new(
    block: UserPreferencesKt.Dsl.() -> Unit
) = toExt()
    .toProto()
    .copy(block)