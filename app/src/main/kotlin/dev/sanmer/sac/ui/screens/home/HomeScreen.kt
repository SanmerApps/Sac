package dev.sanmer.sac.ui.screens.home

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.sanmer.sac.R
import dev.sanmer.sac.ui.activity.MainActivity.Companion.LocalWindowBounds
import dev.sanmer.sac.ui.activity.MainActivity.Companion.hideSystemBars
import dev.sanmer.sac.ui.activity.MainActivity.Companion.setOrientationSensorLandscape
import dev.sanmer.sac.ui.activity.MainActivity.Companion.setOrientationUnspecified
import dev.sanmer.sac.ui.activity.MainActivity.Companion.showSystemBars
import dev.sanmer.sac.ui.navigation.navigateToSettings
import dev.sanmer.sac.ui.providable.LocalUserPreferences
import dev.sanmer.sac.ui.screens.home.items.ErrorItem
import dev.sanmer.sac.ui.screens.home.items.FileItem
import dev.sanmer.sac.ui.screens.home.items.HeaderItem
import dev.sanmer.sac.ui.screens.home.items.WaveformItem
import dev.sanmer.sac.viewmodel.HomeViewModel
import org.jetbrains.letsPlot.skia.compose.PlotPanel
import timber.log.Timber

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val windowBounds = LocalWindowBounds.current
    val isHorizontal = windowBounds.let { it.width > it.height }
    val activity = context as Activity

    LaunchedEffect(viewModel.isFullScreen) {
        if (viewModel.isFullScreen) {
            if (!isHorizontal) activity.setOrientationSensorLandscape()
            activity.hideSystemBars()
        } else {
            if (isHorizontal) activity.setOrientationUnspecified()
            activity.showSystemBars()
        }
    }

    BackHandler(viewModel.isFullScreen) {
        viewModel.isFullScreen = false
    }

    Crossfade(
        targetState = viewModel.isFullScreen,
        label = "HomeScreen"
    ) { isFullScreen ->
        when {
            isFullScreen -> FullContent(viewModel)
            else -> NormalContent(navController, viewModel)
        }
    }
}

@Composable
private fun FullContent(
    viewModel: HomeViewModel
) = Box(
    modifier = Modifier.fillMaxSize()
) {
    PlotPanel(
        figure = viewModel.figure,
        preserveAspectRatio = false,
        modifier = Modifier.fillMaxSize(),
        computationMessagesHandler = { messages ->
            messages.forEach {
                Timber.d("[PlotPanel] $it")
            }
        }
    )

    FloatingActionButton(
        onClick = { viewModel.isFullScreen = false },
        modifier = Modifier
            .padding(top = 5.dp, end = 5.dp)
            .align(Alignment.TopEnd)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.window_minimize),
            contentDescription = null,
            modifier = Modifier.size(30.dp)
        )
    }
}

@Composable
private fun NormalContent(
    navController: NavController,
    viewModel: HomeViewModel
) {
    val userPreferences = LocalUserPreferences.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                navController = navController,
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FileItem(
                filename = viewModel.filename,
                loadSacFile = viewModel::loadSacFile,
                endian = userPreferences.endian,
                onEndianChange = viewModel::setEndian
            )

            if (viewModel.isHeaderReady) {
                HeaderItem(header = viewModel.header)
            }

            if (viewModel.isFigureReady) {
                WaveformItem(
                    figure = viewModel.figure,
                    onMaximize = { viewModel.isFullScreen = true }
                )
            }

            if (viewModel.isFailed) {
                ErrorItem(error = viewModel.error)
            }
        }
    }
}

@Composable
private fun TopBar(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior
) = TopAppBar(
    title = { Text(text = stringResource(id = R.string.app_name)) },
    actions = {
        IconButton(
            onClick = { navController.navigateToSettings() }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.settings),
                contentDescription = null
            )
        }
    },
    scrollBehavior = scrollBehavior
)