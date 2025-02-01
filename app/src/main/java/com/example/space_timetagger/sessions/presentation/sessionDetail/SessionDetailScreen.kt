package com.example.space_timetagger.sessions.presentation.sessionDetail

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.space_timetagger.R
import com.example.space_timetagger.core.presentation.Error
import com.example.space_timetagger.core.presentation.MyScaffold
import com.example.space_timetagger.core.presentation.MyTopBar
import com.example.space_timetagger.core.presentation.TopBarSettingsIcon
import com.example.space_timetagger.sessions.presentation.models.SessionDetailUiModel
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme

@Composable
fun SessionDetailScreen(
    id: String,
    onBackTap: () -> Unit,
    onSettingsTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val viewModel = viewModel<SessionViewModel>(key = id, factory = SessionViewModelFactory(id))
    val viewState by viewModel.viewState.collectAsStateWithLifecycle(SessionDetailViewState.Loading)

    val tagBeingAddedToast = Toast.makeText(context, R.string.tag_being_added, Toast.LENGTH_SHORT)

    fun onEvent(event: SessionDetailEvent) {
        viewModel.handleEvent(event)
        when (event) {
            is SessionDetailEvent.TapBack -> onBackTap()
            is SessionDetailEvent.TapSettings -> onSettingsTap()
            // TODO: move into SessionDetailView and add UI tests?
            is SessionDetailEvent.TapNewTagButton -> {
                tagBeingAddedToast.show()
            }

            is SessionDetailEvent.TapAnywhere -> {
                tagBeingAddedToast.show()
            }

            else -> {}
        }
    }

    SessionDetailView(
        viewState = viewState,
        onEvent = ::onEvent,
        modifier = modifier
    )
}

@Composable
fun SessionDetailView(
    viewState: SessionDetailViewState,
    onEvent: (SessionDetailEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    MyScaffold(
        topBar = {
            SessionDetailTopBar(
                // eventually use session name here, instead of below
                title = stringResource(R.string.session_detail),
                onBackTap = { onEvent(SessionDetailEvent.TapBack) },
                onSettingsTap = { onEvent(SessionDetailEvent.TapSettings) },
            )
        },
        modifier = modifier,
    ) {
        Box(
            it
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (viewState) {
                is SessionDetailViewState.Loading -> CircularProgressIndicator(
                    Modifier.align(Alignment.Center)
                )

                is SessionDetailViewState.Success -> SessionDetail(
                    session = viewState.session,
                    onEvent = onEvent,
                    modifier = Modifier.padding(8.dp)
                )

                is SessionDetailViewState.Refreshing -> {
                    SessionDetail(
                        session = viewState.session,
                        onEvent = onEvent,
                        modifier = Modifier.padding(8.dp)
                    )
                    CircularProgressIndicator(modifier.align(Alignment.Center))
                }

                SessionDetailViewState.Error -> Error(
                    stringResource(R.string.error_session_detail),
                    Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun SessionDetailTopBar(
    title: String,
    onBackTap: () -> Unit,
    onSettingsTap: () -> Unit,
) {
    MyTopBar(
        title = title,
        onBackTap = onBackTap,
    ) {
        TopBarSettingsIcon(onTap = onSettingsTap)
    }
}

class ViewStateProvider : PreviewParameterProvider<SessionDetailViewState> {
    override val values = sequenceOf(
        SessionDetailViewState.Success(
            SessionDetailUiModel(
                id = "id",
                name = "Shopping Trip",
                nameIsBeingEdited = false,
                tags = someTags,
                justAddedTagId = someTags.last().id,
                deleteAllIsEnabled = true,
                tapAnywhereIsEnabled = false,
            ),
        ),
        SessionDetailViewState.Success(
            SessionDetailUiModel(
                id = "id",
                name = "Session with no tags",
                nameIsBeingEdited = false,
                tags = noTags,
                justAddedTagId = null,
                deleteAllIsEnabled = false,
                tapAnywhereIsEnabled = false,
            ),
        ),
        SessionDetailViewState.Success(
            SessionDetailUiModel(
                id = "id",
                name = "Wed commute home",
                nameIsBeingEdited = false,
                tags = manyTags,
                justAddedTagId = manyTags.last().id,
                deleteAllIsEnabled = true,
                tapAnywhereIsEnabled = true,
            ),
        ),
        SessionDetailViewState.Loading,
        SessionDetailViewState.Error,
        SessionDetailViewState.Refreshing(
            SessionDetailUiModel(
                id = "id",
                name = "Refreshing 10pm",
                nameIsBeingEdited = false,
                tags = someTags,
                justAddedTagId = null,
                deleteAllIsEnabled = true,
                tapAnywhereIsEnabled = true,
            ),
        ),
    )
}

@PreviewLightDark
@Preview(device = Devices.TABLET)
@Composable
private fun SessionDetailPreview(
    @PreviewParameter(ViewStateProvider::class) viewState: SessionDetailViewState,
) {
    SpaceTimeTaggerTheme {
        SessionDetailView(viewState, {})
    }
}
