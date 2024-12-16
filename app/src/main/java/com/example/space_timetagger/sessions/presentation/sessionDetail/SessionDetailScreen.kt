package com.example.space_timetagger.sessions.presentation.sessionDetail

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.space_timetagger.R
import com.example.space_timetagger.core.presentation.Error
import com.example.space_timetagger.sessions.presentation.models.SessionDetailUiModel
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme

@Composable
fun SessionDetailScreen(
    id: String,
    modifier: Modifier = Modifier,
) {
    val viewModel = viewModel<SessionViewModel>(key = id, factory = SessionViewModelFactory(id))
    val viewState by viewModel.viewState.collectAsStateWithLifecycle(SessionDetailViewState.Loading)

    SessionDetailView(
        viewState = viewState,
        onEvent = viewModel::handleEvent,
        modifier = modifier
    )
}

@Composable
fun SessionDetailView(
    viewState: SessionDetailViewState,
    onEvent: (SessionDetailEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (viewState) {
            is SessionDetailViewState.Loading -> CircularProgressIndicator(modifier.align(Alignment.Center))

            is SessionDetailViewState.Success -> SessionDetail(
                session = viewState.session,
                onEvent = onEvent,
                modifier = modifier.padding(8.dp)
            )

            is SessionDetailViewState.Refreshing -> {
                SessionDetail(
                    session = viewState.session,
                    onEvent = onEvent,
                    modifier = modifier.padding(8.dp)
                )
                CircularProgressIndicator(modifier.align(Alignment.Center))
            }

            SessionDetailViewState.Error -> Error(
                stringResource(R.string.error_session_detail),
                modifier.align(Alignment.Center)
            )
        }
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
                deleteAllIsEnabled = true,
            ),
        ),
        SessionDetailViewState.Success(
            SessionDetailUiModel(
                id = "id",
                name = "Session with no tags",
                nameIsBeingEdited = false,
                tags = noTags,
                deleteAllIsEnabled = false,
            ),
        ),
        SessionDetailViewState.Success(
            SessionDetailUiModel(
                id = "id",
                name = "Wed commute home",
                nameIsBeingEdited = false,
                tags = manyTags,
                deleteAllIsEnabled = true,
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
                deleteAllIsEnabled = true,
            ),
        ),
    )
}

@PreviewLightDark
@Preview(widthDp = 720, heightDp = 360)
@Composable
private fun SessionDetailPreview(
    @PreviewParameter(ViewStateProvider::class) viewState: SessionDetailViewState,
) {
    SpaceTimeTaggerTheme {
        SessionDetailView(viewState, {})
    }
}
