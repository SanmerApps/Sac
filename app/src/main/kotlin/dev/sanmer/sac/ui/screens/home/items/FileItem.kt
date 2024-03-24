package dev.sanmer.sac.ui.screens.home.items

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import dev.sanmer.sac.R
import dev.sanmer.sac.io.Endian
import dev.sanmer.sac.ui.component.DropdownMenu
import dev.sanmer.sac.ui.component.OverviewCard

@Composable
fun FileItem(
    filename: String,
    loadSacFile: (Uri, Endian) -> Unit,
    endian: Endian,
    onEndianChange: (Endian) -> Unit
) = OverviewCard(
    expanded = true,
    label = stringResource(id = R.string.home_file),
    icon = R.drawable.file_database,
    trailingIcon = {
        EndianSelect(
            selected = endian,
            onChange = onEndianChange
        )
    }
) {
    val interactionSource = remember { MutableInteractionSource() }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        loadSacFile(uri, endian)
    }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                launcher.launch("application/octet-stream")
            }
        }
    }

    Surface(
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(1.2.dp, color = MaterialTheme.colorScheme.outline),
        onClick = {},
        interactionSource = interactionSource
    ) {
        Text(
            text = filename.ifEmpty { stringResource(id = R.string.home_file_placeholder) },
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(all = 15.dp)
                .fillMaxWidth(),
        )
    }
}

@Composable
private fun EndianSelect(
    selected: Endian,
    onChange: (Endian) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val animateZ by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "animateZ"
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        offset = DpOffset(0.dp, 10.dp),
        contentAlignment = Alignment.BottomEnd,
        surface = {
            FilterChip(
                modifier = Modifier.height(FilterChipDefaults.Height),
                selected = true,
                onClick = { expanded = true },
                label = { Text(text = selected.name()) },
                trailingIcon = {
                    Icon(
                        modifier = Modifier
                            .size(16.dp)
                            .graphicsLayer {
                                rotationZ = animateZ
                            },
                        painter = painterResource(id = R.drawable.caret_down_filled),
                        contentDescription = null
                    )
                }
            )
        }
    ) {
        MenuItem(
            name = stringResource(id = R.string.home_endian_little),
            selected = selected == Endian.Little,
            onClick = {
                onChange(Endian.Little)
                expanded = false
            }
        )

        MenuItem(
            name = stringResource(id = R.string.home_endian_big),
            selected = selected == Endian.Big,
            onClick = {
                onChange(Endian.Big)
                expanded = false
            }
        )
    }
}

@Composable
private fun MenuItem(
    name: String,
    selected: Boolean,
    onClick: () -> Unit
) = DropdownMenuItem(
    modifier = Modifier
        .background(
            if (selected) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                Color.Unspecified
            }
        ),
    text = { Text(text = name) },
    onClick = onClick
)

@Composable
private fun Endian.name() = when (this) {
    Endian.Little -> stringResource(id = R.string.home_endian_little)
    Endian.Big -> stringResource(id = R.string.home_endian_big)
}