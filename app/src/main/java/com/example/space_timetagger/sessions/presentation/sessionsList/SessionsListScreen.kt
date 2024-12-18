package com.example.space_timetagger.sessions.presentation.sessionsList

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
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme

@Composable
fun SessionsListScreen(
    modifier: Modifier = Modifier,
    navigateToSession: (String) -> Unit,
) {
    val viewModel = viewModel<SessionsViewModel>(factory = SessionsViewModelFactory())
    val viewState by viewModel.viewState.collectAsStateWithLifecycle(SessionsListViewState.Loading)

    HandleNavigatingToNewSession(viewModel.sessionIdToNavigateTo, navigateToSession)

    fun onEvent(event: SessionsListEvent) {
        viewModel.handleEvent(event)
    }

    SessionsListView(
        viewState = viewState,
        onEvent = ::onEvent,
        modifier = modifier
    )
}

@Composable
fun SessionsListView(
    viewState: SessionsListViewState,
    onEvent: (SessionsListEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (viewState) {
            is SessionsListViewState.Loading -> CircularProgressIndicator(
                modifier = modifier.align(Alignment.Center)
            )

            is SessionsListViewState.Success -> Sessions(
                sessions = viewState.sessions,
                deleteAllIsEnabled = viewState.deleteAllIsEnabled,
                onEvent = onEvent,
                modifier = Modifier.padding(8.dp)
            )

            is SessionsListViewState.Refreshing -> {
                Sessions(
                    sessions = viewState.sessions,
                    deleteAllIsEnabled = false,
                    onEvent = onEvent,
                    modifier = Modifier.padding(8.dp)
                )
                CircularProgressIndicator(modifier.align(Alignment.Center))
            }

            is SessionsListViewState.Error -> Error(
                text = stringResource(R.string.error_sessions_list),
                modifier = modifier.align(Alignment.Center)
            )
        }
    }
}

class SessionsListStateProvider : PreviewParameterProvider<SessionsListViewState> {
    override val values = sequenceOf(
        SessionsListViewState.Success(someSessions, true),
        SessionsListViewState.Success(noSessions, false),
        SessionsListViewState.Loading,
        SessionsListViewState.Error,
        SessionsListViewState.Refreshing(someSessions),
    )
}

@Suppress("EmptyFunctionBlock")
@PreviewLightDark
@Preview(widthDp = 720, heightDp = 360)
@Composable
private fun SessionsPreview(
    @PreviewParameter(SessionsListStateProvider::class) viewState: SessionsListViewState,
) {
    SpaceTimeTaggerTheme {
        SessionsListView(viewState, {})
    }
}
