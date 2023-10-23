package dev.sanmer.sac.ui.screens.home.items

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.sanmer.sac.R
import dev.sanmer.sac.io.SacHeader
import dev.sanmer.sac.ui.component.OverviewCard

@Composable
fun HeaderItem(
    header: SacHeader
) {
    var expanded by remember { mutableStateOf(false) }
    val animateZ by animateFloatAsState(
        targetValue = if (expanded) 0f else -90f,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "animateZ"
    )

    OverviewCard(
        expanded = expanded,
        label = stringResource(id = R.string.home_header),
        icon = R.drawable.clipboard_text,
        trailingIcon = {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    modifier = Modifier
                        .graphicsLayer {
                            rotationZ = animateZ
                        },
                    painter = painterResource(id = R.drawable.caret_down),
                    contentDescription = null
                )
            }
        }
    ) {
        Surface(
            shape = RoundedCornerShape(15.dp),
            border = BorderStroke(1.2.dp, color = MaterialTheme.colorScheme.outline)
        ) {
            ValueList(
                header = header
            )
        }
    }
}

@Suppress("SpellCheckingInspection")
@Composable
private fun ValueList(
    header: SacHeader
) = Column(
    modifier = Modifier
        .fillMaxWidth()
        .padding(all = 15.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
) {
    ValueItem(key = "delta", value = header.delta)
    ValueItem(key = "depmin", value = header.depmin)
    ValueItem(key = "depmax", value = header.depmax)
    ValueItem(key = "scale", value = header.scale)
    ValueItem(key = "odelta", value = header.odelta)
    ValueItem(key = "b", value = header.b)
    ValueItem(key = "e", value = header.e)
    ValueItem(key = "o", value = header.o)
    ValueItem(key = "a", value = header.a)

    header.t.forEachIndexed { i, v ->
        ValueItem(key = "t$i", value = v)
    }

    ValueItem(key = "f", value = header.f)

    header.resp.forEachIndexed { i, v ->
        ValueItem(key = "resp$i", value = v)
    }

    ValueItem(key = "stla", value = header.stla)
    ValueItem(key = "stlo", value = header.stlo)
    ValueItem(key = "stel", value = header.stel)
    ValueItem(key = "stdp", value = header.stdp)
    ValueItem(key = "evla", value = header.evla)
    ValueItem(key = "evlo", value = header.evlo)
    ValueItem(key = "evel", value = header.evel)
    ValueItem(key = "evdp", value = header.evdp)
    ValueItem(key = "mag", value = header.mag)

    header.user.forEachIndexed { i, v ->
        ValueItem(key = "user$i", value = v)
    }

    ValueItem(key = "dist", value = header.dist)
    ValueItem(key = "az", value = header.az)
    ValueItem(key = "baz", value = header.baz)
    ValueItem(key = "gcarc", value = header.gcarc)
    ValueItem(key = "depmen", value = header.depmen)
    ValueItem(key = "cmpaz", value = header.cmpaz)
    ValueItem(key = "cmpinc", value = header.cmpinc)
    ValueItem(key = "xminimum", value = header.xminimum)
    ValueItem(key = "xmaximum", value = header.xmaximum)
    ValueItem(key = "yminimum", value = header.yminimum)
    ValueItem(key = "ymaximum", value = header.ymaximum)

    ValueItem(key = "nzyear", value = header.nzyear)
    ValueItem(key = "nzjday", value = header.nzjday)
    ValueItem(key = "nzhour", value = header.nzhour)
    ValueItem(key = "nzmin", value = header.nzmin)
    ValueItem(key = "nzsec", value = header.nzsec)
    ValueItem(key = "nzmsec", value = header.nzmsec)
    ValueItem(key = "nvhdr", value = header.nvhdr)
    ValueItem(key = "norid", value = header.norid)
    ValueItem(key = "nevid", value = header.nevid)
    ValueItem(key = "npts", value = header.npts)
    ValueItem(key = "nwfid", value = header.nwfid)
    ValueItem(key = "nxsize", value = header.nxsize)
    ValueItem(key = "nysize", value = header.nysize)
    ValueItem(key = "iftype", value = header.iftype)
    ValueItem(key = "idep", value = header.idep)
    ValueItem(key = "iztype", value = header.iztype)
    ValueItem(key = "iinst", value = header.iinst)
    ValueItem(key = "istreg", value = header.istreg)
    ValueItem(key = "ievreg", value = header.ievreg)
    ValueItem(key = "ievtyp", value = header.ievtyp)
    ValueItem(key = "iqual", value = header.iqual)
    ValueItem(key = "isynth", value = header.isynth)
    ValueItem(key = "imagtyp", value = header.imagtyp)
    ValueItem(key = "imagsrc", value = header.imagsrc)

    ValueItem(key = "leven", value = header.leven)
    ValueItem(key = "lpspol", value = header.lpspol)
    ValueItem(key = "lovrok", value = header.lovrok)
    ValueItem(key = "lcalda", value = header.lcalda)

    ValueItem(key = "kstnm", value = header.kstnm)
    ValueItem(key = "kevnm", value = header.kevnm)
    ValueItem(key = "khole", value = header.khole)
    ValueItem(key = "ko", value = header.ko)
    ValueItem(key = "ka", value = header.ka)

    header.kt.forEachIndexed { i, v ->
        ValueItem(key = "kt$i", value = v)
    }

    ValueItem(key = "kf", value = header.kf)
    ValueItem(key = "kuser0", value = header.kuser0)
    ValueItem(key = "kuser1", value = header.kuser1)
    ValueItem(key = "kuser2", value = header.kuser2)
    ValueItem(key = "kcmpnm", value = header.kcmpnm)
    ValueItem(key = "knetwk", value = header.knetwk)
    ValueItem(key = "kdatrd", value = header.kdatrd)
    ValueItem(key = "kinst", value = header.kinst)
}

@Composable
private fun ValueItem(
    key: String,
    value: Any,
    modifier: Modifier = Modifier
) {
    val valueString = value.toString()
    if (valueString.contains("-12345")) return

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = key,
            style = MaterialTheme.typography.bodyLarge,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(0.5f)
        )

        Text(
            text = valueString,
            style = MaterialTheme.typography.bodyLarge,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Start
        )
    }
}