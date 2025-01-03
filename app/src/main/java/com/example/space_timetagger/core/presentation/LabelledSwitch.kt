package com.example.space_timetagger.core.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme

@Composable
fun LabelledSwitch(
    name: String,
    isChecked: Boolean,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onTap)
            .padding(horizontal = 8.dp)
    ) {
        Text(text = name)
        Spacer(Modifier.weight(1f))
        Switch(
            checked = isChecked,
            // if it's null then ui tests won't recognize it as toggleable
            onCheckedChange = {},
        )
    }
}

@Suppress("EmptyFunctionBlock")
@PreviewLightDark
@Preview(device = Devices.TABLET)
@Composable
private fun LabelledSwitchPreviewOn() {
    SpaceTimeTaggerTheme {
        LabelledSwitch("Custom Setting", true, { })
    }
}

@Suppress("EmptyFunctionBlock")
@PreviewLightDark
@Preview(device = Devices.TABLET)
@Composable
private fun LabelledSwitchPreviewOff() {
    SpaceTimeTaggerTheme {
        LabelledSwitch("Custom Setting", false, {})
    }
}
