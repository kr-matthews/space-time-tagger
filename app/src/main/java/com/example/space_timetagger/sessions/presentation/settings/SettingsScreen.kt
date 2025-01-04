package com.example.space_timetagger.sessions.presentation.settings

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.space_timetagger.R
import com.example.space_timetagger.core.presentation.InfoDialog
import com.example.space_timetagger.core.presentation.LabelledSwitch
import com.example.space_timetagger.core.presentation.MyScaffold
import com.example.space_timetagger.core.presentation.MyTopBar
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme

@Composable
fun SettingsScreen(
    onBackTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = viewModel<SettingsViewModel>(factory = SettingsViewModelFactory())
    val viewState by viewModel.viewState.collectAsStateWithLifecycle(SettingsViewState.Loading)
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
    MyScaffold(
        topBar = { SettingsTopBar(onBackTap = { onEvent(SettingsEvent.TapBack) }) },
        modifier = modifier
    ) {
        when (viewState) {
            SettingsViewState.Loading -> SettingsLoading(modifier = it)

            is SettingsViewState.Success -> {
                SettingsContent(
                    viewState = viewState,
                    onEvent = onEvent,
                    modifier = it
                )
                LocationPermissionHandling(
                    viewState = viewState,
                    onEvent = onEvent,
                )
            }
        }
    }
}

@Composable
private fun SettingsTopBar(
    onBackTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MyTopBar(
        title = stringResource(R.string.settings),
        onBackTap = onBackTap,
        modifier = modifier
    )
}

@Composable
fun SettingsContent(
    viewState: SettingsViewState.Success,
    onEvent: (SettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val hasLocationPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        LabelledSwitch(
            name = stringResource(R.string.capture_location),
            isChecked = viewState.taggingLocationIsEnabled,
            onTap = { onEvent(SettingsEvent.TapLocationTaggingToggle(hasLocationPermission)) },
        )
    }
}

@Composable
fun LocationPermissionHandling(
    viewState: SettingsViewState.Success,
    onEvent: (SettingsEvent) -> Unit,
) {
    val permissionRequestLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { isGranted ->
            if (isGranted[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                onEvent(SettingsEvent.LocationPermissionGranted)
            } else {
                onEvent(SettingsEvent.LocationPermissionDenied)
            }
        }
    )

    val locationPermissionMustBeRequested = viewState.locationPermissionMustBeRequested

    LaunchedEffect(locationPermissionMustBeRequested) {
        if (locationPermissionMustBeRequested) {
            permissionRequestLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                )
            )
            onEvent(SettingsEvent.LocationPermissionRequestLaunched)
        }
    }

    if (viewState.locationPermissionExplanationIsVisible) {
        InfoDialog(
            text = stringResource(R.string.why_location_permission_is_required),
            onDismiss = { onEvent(SettingsEvent.LocationPermissionDialogDismissed) },
        )
    }
}

@Composable
fun SettingsLoading(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CircularProgressIndicator(
            Modifier.align(Alignment.Center)
        )
    }
}

class SettingsStateProvider : PreviewParameterProvider<SettingsViewState> {
    override val values = sequenceOf(
        SettingsViewState.Loading,
        SettingsViewState.Success(
            taggingLocationIsEnabled = false,
            locationPermissionMustBeRequested = false,
            locationPermissionExplanationIsVisible = false,
        ),
        SettingsViewState.Success(
            taggingLocationIsEnabled = false,
            locationPermissionMustBeRequested = true,
            locationPermissionExplanationIsVisible = false,
        ),
        SettingsViewState.Success(
            taggingLocationIsEnabled = false,
            locationPermissionMustBeRequested = false,
            locationPermissionExplanationIsVisible = true,
        ),
        SettingsViewState.Success(
            taggingLocationIsEnabled = true,
            locationPermissionMustBeRequested = false,
            locationPermissionExplanationIsVisible = false,
        ),
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