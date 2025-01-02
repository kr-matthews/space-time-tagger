package com.example.space_timetagger.sessions.presentation.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme

@Composable
fun SettingsScreen(
    onBackTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = viewModel<SettingsViewModel>(factory = SettingsViewModelFactory())
    val viewState by viewModel.viewState.collectAsStateWithLifecycle(SettingsViewState())
    fun onEvent(event: SettingsEvent) {
        viewModel.handleEvent(event)
        when (event) {
            is SettingsEvent.TapBack -> onBackTap()
            else -> Unit
        }
    }

    SettingsView(
        viewState = viewState,
        onEvent = ::onEvent,
        modifier = modifier,
    )
}

@Composable
fun SettingsView(
    viewState: SettingsViewState,
    onEvent: (SettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    // TODO
}

class SettingsStateProvider : PreviewParameterProvider<SettingsViewState> {
    override val values = sequenceOf(
        SettingsViewState(false),
        SettingsViewState(true),
    )
}

@Suppress("EmptyFunctionBlock")
@PreviewLightDark
@Preview(device = Devices.TABLET)
@Composable
private fun SettingsViewPreview(
    @PreviewParameter(SettingsStateProvider::class) viewState: SettingsViewState,
) {
    SpaceTimeTaggerTheme {
        SettingsView(viewState, {})
    }
}
