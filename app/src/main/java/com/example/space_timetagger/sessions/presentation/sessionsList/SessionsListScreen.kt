package com.example.space_timetagger.sessions.presentation.sessionsList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.space_timetagger.R
import com.example.space_timetagger.core.presentation.Error
import com.example.space_timetagger.core.presentation.MyScaffold
import com.example.space_timetagger.core.presentation.MyTopBar
import com.example.space_timetagger.core.presentation.ScreenPreviews
import com.example.space_timetagger.core.presentation.TopBarSettingsIcon
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme

@Composable
fun SessionsListScreen(
    onSettingsTap: () -> Unit,
    onNavigateToSession: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = viewModel<SessionsListViewModel>(factory = SessionsViewModelFactory())
    val viewState by viewModel.viewState.collectAsStateWithLifecycle(SessionsListViewState.Loading)

    fun onEvent(event: SessionsListEvent) {
        viewModel.handleEvent(event)
        when (event) {
            is SessionsListEvent.TapSettings -> onSettingsTap()
            is SessionsListEvent.TapSession -> onNavigateToSession(event.sessionId)
            else -> Unit
        }
    }

    val idToNavigateTo = (viewState as? SessionsListViewState.Success)?.idToNavigateTo
    val sessions = (viewState as? SessionsListViewState.Success)?.sessions

    LaunchedEffect(key1 = idToNavigateTo, key2 = sessions) {
        sessions?.find {
            it.id == idToNavigateTo
        }?.let {
            onNavigateToSession(it.id)
            onEvent(SessionsListEvent.AutoNavigateToSession(it.id))
        }
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
    MyScaffold(
        topBar = {
            SessionsListTopBar(
                title = stringResource(R.string.sessions),
                onSettingsTap = { onEvent(SessionsListEvent.TapSettings) },
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
}

@Composable
private fun SessionsListTopBar(
    title: String,
    onSettingsTap: () -> Unit,
) {
    MyTopBar(
        title = title,
    ) {
        TopBarSettingsIcon(onTap = onSettingsTap)
    }
}

class SessionsListStateProvider : PreviewParameterProvider<SessionsListViewState> {
    override val values = sequenceOf(
        SessionsListViewState.Success(someSessions, null, true),
        SessionsListViewState.Success(noSessions, null, false),
        SessionsListViewState.Loading,
        SessionsListViewState.Error,
        SessionsListViewState.Refreshing(someSessions),
    )
}

@ScreenPreviews
@Composable
private fun SessionsPreview(
    @PreviewParameter(SessionsListStateProvider::class) viewState: SessionsListViewState,
) {
    SpaceTimeTaggerTheme {
        SessionsListView(viewState, {})
    }
}
