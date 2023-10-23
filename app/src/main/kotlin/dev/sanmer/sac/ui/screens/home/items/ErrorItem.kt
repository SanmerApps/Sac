package dev.sanmer.sac.ui.screens.home.items

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.sac.R
import dev.sanmer.sac.ui.component.OverviewCard

@Composable
fun ErrorItem(
    error: String
) = OverviewCard(
    expanded = true,
    label = stringResource(id = R.string.home_error),
    icon = R.drawable.bug
){
    Surface(
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(1.2.dp, color = MaterialTheme.colorScheme.outline)
    ) {
        Text(
            text = error,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(all = 15.dp)
        )
    }
}