package com.example.space_timetagger.core.presentation

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Suppress("EmptyFunctionBlock")
@Preview(
    name = "Tablet",
    device = Devices.TABLET,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showSystemUi = true,
)
@Preview(
    name = "Light Landscape",
    device = "spec:parent=pixel_5,orientation=landscape",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showSystemUi = true,
)
@Preview(
    name = "Dark Portrait",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showSystemUi = true,
)
annotation class ScreenPreviews

@Suppress("EmptyFunctionBlock")
@Preview(
    name = "Tablet", device = Devices.TABLET, uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Preview(
    name = "Light",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
    backgroundColor = 0xFFFFFBFE,
)
@Preview(
    name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    backgroundColor = 0xFF1F1B24,
)
annotation class ComponentPreviews
