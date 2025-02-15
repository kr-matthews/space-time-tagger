package com.example.space_timetagger.core.presentation

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark

@Suppress("EmptyFunctionBlock")
@Preview(name = "Tablet", device = Devices.TABLET, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Light Landscape", heightDp = 360, widthDp = 800)
@Preview(name = "Dark Portrait", uiMode = Configuration.UI_MODE_NIGHT_YES)
annotation class ScreenPreviews

@Suppress("EmptyFunctionBlock")
@Preview(name = "Tablet", device = Devices.TABLET, uiMode = Configuration.UI_MODE_NIGHT_YES)
@PreviewLightDark
annotation class ComponentPreviews

