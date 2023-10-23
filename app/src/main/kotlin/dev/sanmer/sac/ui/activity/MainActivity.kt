package dev.sanmer.sac.ui.activity

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.toComposeRect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.window.layout.WindowMetricsCalculator
import dagger.hilt.android.AndroidEntryPoint
import dev.sanmer.sac.ui.theme.AppTheme
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val metrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(this)
        val windowBounds = metrics.bounds.toComposeRect()

        setContent {
            CompositionLocalProvider(
                LocalWindowBounds provides windowBounds
            ) {
                AppTheme {
                    MainScreen()
                }
            }
        }
    }

    companion object {
        val LocalWindowBounds = staticCompositionLocalOf { Rect.Zero }

        fun Activity.setOrientationSensorLandscape() {
            Timber.d("setOrientationSensorLandscape")
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        }

        fun Activity.setOrientationUnspecified() {
            Timber.d("setOrientationUnspecified")
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }

        fun Activity.hideSystemBars() {
            Timber.d("hideSystemBars")
            val windowInsetsController =
                WindowCompat.getInsetsController(window, window.decorView)

            windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        }

        fun Activity.showSystemBars() {
            Timber.d("showSystemBars")
            val windowInsetsController =
                WindowCompat.getInsetsController(window, window.decorView)

            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
        }
    }
}