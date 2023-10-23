package dev.sanmer.sac.app.utils

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

object NotificationUtils {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    fun PermissionState() {
        val permissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

        SideEffect {
            if (!permissionState.status.isGranted) {
                permissionState.launchPermissionRequest()
            }
        }
    }
}