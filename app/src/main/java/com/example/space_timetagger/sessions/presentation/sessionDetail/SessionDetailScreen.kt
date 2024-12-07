package com.example.space_timetagger.sessions.presentation.sessionDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.space_timetagger.R
import com.example.space_timetagger.core.presentation.Error
import com.example.space_timetagger.sessions.domain.models.SessionCallbacks
import com.example.space_timetagger.sessions.presentation.models.SessionDetailUi
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme

@Composable
fun SessionDetailScreen(
    id: String,
    modifier: Modifier = Modifier,
) {
    val viewModel = viewModel<SessionViewModel>(key = id, factory = SessionViewModelFactory(id))
    val viewState by viewModel.viewState.collectAsState(SessionDetailState.Loading)

    SessionDetailView(viewState, viewModel.callbacks, modifier)
}

@Composable
fun SessionDetailView(
    viewState: SessionDetailState,
    callbacks: SessionCallbacks,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (viewState) {
            is SessionDetailState.Loading -> CircularProgressIndicator(modifier.align(Alignment.Center))
            is SessionDetailState.Success -> SessionDetail(
                viewState.session.name,
                viewState.session.tags,
                callbacks,
                modifier.padding(8.dp)
            )

            is SessionDetailState.Refreshing -> {
                SessionDetail(
                    viewState.session.name,
                    viewState.session.tags,
                    callbacks,
                    modifier.padding(8.dp)
                )
                CircularProgressIndicator(modifier.align(Alignment.Center))
            }

            SessionDetailState.Error -> Error(
                stringResource(R.string.error_session_detail),
                modifier.align(Alignment.Center)
            )
        }
    }
}

class ViewStateProvider : PreviewParameterProvider<SessionDetailState> {
    override val values = sequenceOf(
        SessionDetailState.Success(
            SessionDetailUi(
                id = "id",
                name = "Shopping Trip",
                tags = someTags,
            ),
        ),
        SessionDetailState.Success(
            SessionDetailUi(
                id = "id",
                name = "Session with no tags",
                tags = noTags,
            ),
        ),
        SessionDetailState.Success(
            SessionDetailUi(
                id = "id",
                name = "Wed commute home",
                tags = manyTags,
            ),
        ),
        SessionDetailState.Loading,
        SessionDetailState.Error,
        SessionDetailState.Refreshing(
            SessionDetailUi(
                id = "id",
                name = "Refreshing 10pm",
                tags = someTags,
            ),
        ),
    )
}

@PreviewLightDark
@Preview(widthDp = 720, heightDp = 360)
@Composable
private fun SessionDetailPreview(
    @PreviewParameter(ViewStateProvider::class) viewState: SessionDetailState,
) {
    SpaceTimeTaggerTheme {
        SessionDetailView(viewState, dummySessionCallbacks)
    }
}
