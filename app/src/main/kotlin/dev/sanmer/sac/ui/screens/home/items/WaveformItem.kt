package dev.sanmer.sac.ui.screens.home.items

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.sac.R
import dev.sanmer.sac.ui.component.OverviewCard
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.skia.compose.PlotPanel
import timber.log.Timber

@Composable
fun WaveformItem(
    figure: Figure,
    onMaximize: () -> Unit
) {
    OverviewCard(
        expanded = true,
        label = stringResource(id = R.string.home_waveform),
        icon = R.drawable.chart_line,
        trailingIcon = {
            IconButton(
                onClick = onMaximize
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.window_maximize),
                    contentDescription = null
                )
            }
        }
    ) {
        PlotPanel(
            figure = figure,
            preserveAspectRatio = false,
            modifier = Modifier
                .clip(RoundedCornerShape(15.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(15.dp)
                )
                .requiredHeight(240.dp)
                .fillMaxWidth(),
            computationMessagesHandler = { messages ->
                messages.forEach {
                    Timber.d("[PlotPanel] $it")
                }
            }
        )
    }
}