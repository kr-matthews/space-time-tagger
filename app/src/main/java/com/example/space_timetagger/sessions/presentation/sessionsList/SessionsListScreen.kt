package com.example.space_timetagger.sessions.presentation.sessionsList

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
import com.example.space_timetagger.sessions.domain.models.SessionsCallbacks
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme

@Composable
fun SessionsListScreen(
    modifier: Modifier = Modifier,
    navigateToSession: (String) -> Unit,
) {
    val viewModel = viewModel<SessionsViewModel>(factory = SessionsViewModelFactory())
    val viewState by viewModel.viewState.collectAsState(SessionsListState.Loading)

    HandleNavigatingToNewSession(viewModel.sessionIdToNavigateTo, navigateToSession)

    SessionsListView(viewState, viewModel.callbacks, navigateToSession, modifier)
}

@Composable
fun SessionsListView(
    viewState: SessionsListState,
    callbacks: SessionsCallbacks,
    navigateToSession: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (viewState) {
            is SessionsListState.Loading -> CircularProgressIndicator(modifier.align(Alignment.Center))
            is SessionsListState.Success -> Sessions(
                viewState.sessions,
                callbacks,
                navigateToSession,
                Modifier.padding(8.dp)
            )

            is SessionsListState.Refreshing -> {
                Sessions(
                    viewState.sessions,
                    callbacks,
                    navigateToSession,
                    Modifier.padding(8.dp)
                )
                CircularProgressIndicator(modifier.align(Alignment.Center))
            }

            is SessionsListState.Error -> Error(
                stringResource(R.string.error_sessions_list),
                modifier.align(Alignment.Center)
            )
        }
    }
}

class SessionsListStateProvider : PreviewParameterProvider<SessionsListState> {
    override val values = sequenceOf(
        SessionsListState.Success(someSessions),
        SessionsListState.Success(noSessions),
        SessionsListState.Loading,
        SessionsListState.Error,
        SessionsListState.Refreshing(someSessions),
    )
}

@Suppress("EmptyFunctionBlock")
@PreviewLightDark
@Preview(widthDp = 720, heightDp = 360)
@Composable
private fun SessionsPreview(
    @PreviewParameter(SessionsListStateProvider::class) viewState: SessionsListState,
) {
    SpaceTimeTaggerTheme {
        SessionsListView(viewState, dummySessionsCallbacks, {})
    }
}
