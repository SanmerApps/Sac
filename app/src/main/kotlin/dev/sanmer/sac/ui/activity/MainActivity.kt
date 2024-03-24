package dev.sanmer.sac.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.toComposeRect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.layout.WindowMetricsCalculator
import dagger.hilt.android.AndroidEntryPoint
import dev.sanmer.sac.datastore.isDarkMode
import dev.sanmer.sac.repository.UserPreferencesRepository
import dev.sanmer.sac.ui.providable.LocalUserPreferences
import dev.sanmer.sac.ui.theme.AppTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var userPreferencesRepository: UserPreferencesRepository

    private var isLoading by mutableStateOf(true)
    private var fileUri by mutableStateOf(Uri.EMPTY)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition { isLoading }

        val metrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(this)
        val windowBounds = metrics.bounds.toComposeRect()

        intent?.data?.let { fileUri = it }

        setContent {
            val userPreferences by userPreferencesRepository.data
                .collectAsStateWithLifecycle(initialValue = null)

            val preferences = if (userPreferences == null) {
                return@setContent
            } else {
                isLoading = false
                checkNotNull(userPreferences)
            }

            CompositionLocalProvider(
                LocalWindowBounds provides windowBounds,
                LocalFileUri provides fileUri,
                LocalUserPreferences provides preferences
            ) {
                AppTheme(
                    darkMode = preferences.isDarkMode(),
                    themeColor = preferences.themeColor
                ) {
                    MainScreen()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.data?.let { fileUri = it }
    }

    companion object {
        val LocalWindowBounds = staticCompositionLocalOf { Rect.Zero }
        val LocalFileUri = staticCompositionLocalOf { Uri.EMPTY }
    }
}