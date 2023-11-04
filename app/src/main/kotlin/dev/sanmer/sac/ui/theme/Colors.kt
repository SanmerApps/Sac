package dev.sanmer.sac.ui.theme

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import dev.sanmer.sac.app.utils.OsUtils
import dev.sanmer.sac.ui.theme.color.BlueDarkColorScheme
import dev.sanmer.sac.ui.theme.color.BlueLightColorScheme
import dev.sanmer.sac.ui.theme.color.CyanDarkColorScheme
import dev.sanmer.sac.ui.theme.color.CyanLightColorScheme
import dev.sanmer.sac.ui.theme.color.DeepPurpleDarkColorScheme
import dev.sanmer.sac.ui.theme.color.DeepPurpleLightColorScheme
import dev.sanmer.sac.ui.theme.color.OrangeDarkColorScheme
import dev.sanmer.sac.ui.theme.color.OrangeLightColorScheme
import dev.sanmer.sac.ui.theme.color.SakuraDarkColorScheme
import dev.sanmer.sac.ui.theme.color.SakuraLightColorScheme

sealed class Colors(
    val id: Int,
    val lightColorScheme: ColorScheme,
    val darkColorScheme: ColorScheme
) {
    @RequiresApi(Build.VERSION_CODES.S)
    class Dynamic(context: Context) : Colors(
        id = id,
        lightColorScheme = dynamicLightColorScheme(context),
        darkColorScheme = dynamicDarkColorScheme(context)
    ) {
        companion object {
            @Suppress("ConstPropertyName")
            const val id = -1
        }
    }
    data object Sakura : Colors(
        id = 0,
        lightColorScheme = SakuraLightColorScheme,
        darkColorScheme = SakuraDarkColorScheme
    )
    data object DeepPurple : Colors(
        id = 1,
        lightColorScheme = DeepPurpleLightColorScheme,
        darkColorScheme = DeepPurpleDarkColorScheme
    )
    data object Blue: Colors(
        id = 2,
        lightColorScheme = BlueLightColorScheme,
        darkColorScheme = BlueDarkColorScheme
    )
    data object Cyan: Colors(
        id = 3,
        lightColorScheme = CyanLightColorScheme,
        darkColorScheme = CyanDarkColorScheme
    )
    data object Orange: Colors(
        id = 4,
        lightColorScheme = OrangeLightColorScheme,
        darkColorScheme = OrangeDarkColorScheme
    )

    companion object {
        private val mColors get() = listOf(
            Sakura,
            DeepPurple,
            Blue,
            Cyan,
            Orange
        )

        fun getColorIds(): List<Int> {
            return mColors.map { it.id }
        }

        @Composable
        fun getColor(id: Int): Colors {
            val context = LocalContext.current

            return if (OsUtils.atLeastS && id == Dynamic.id) {
                Dynamic(context)
            } else {
                mColors[id]
            }
        }
    }
}