package dev.sanmer.sac.ui.activity

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.letsPlot.geom.geomDensity
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.skia.compose.PlotPanel

@Composable
fun TestScreen() {
    // irrelevant value
    var enable by remember { mutableStateOf(false) }

    // remember data and figure
    val rand = java.util.Random()
    val data by remember {
        mutableStateOf(
            mapOf(
                "rating" to List(200) { rand.nextGaussian() } + List(200) { rand.nextGaussian() * 1.5 + 1.5 },
                "cond" to List(200) { "A" } + List(200) { "B" }
            )
        )
    }
    val plot by remember {
        mutableStateOf(letsPlot(data) +
                geomDensity(color = "dark_green", alpha = .3) {
                    x = "rating"; fill = "cond"
                }
        )
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Irrelevant Value",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )

                Switch(
                    checked = enable,
                    onCheckedChange = { enable = it }
                )
            }

            PlotPanel(
                figure = plot,
                preserveAspectRatio = false,
                modifier = Modifier
                    .requiredHeight(240.dp)
                    .fillMaxWidth(),
                computationMessagesHandler = { messages ->
                    messages.forEach {
                        Log.d("PlotPanel", it)
                    }
                }
            )
        }
    }
}