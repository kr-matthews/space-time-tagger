package com.example.space_timetagger.sessions.presentation.sessionsList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.space_timetagger.R
import com.example.space_timetagger.core.presentation.ConfirmationDialog
import com.example.space_timetagger.core.presentation.Error
import com.example.space_timetagger.sessions.domain.models.SessionsCallbacks
import com.example.space_timetagger.sessions.presentation.models.SessionOverviewUi
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@Composable
fun SessionsListScreen(
    modifier: Modifier = Modifier,
    navigateToSession: (String) -> Unit,
) {
    val viewModel = viewModel<SessionsViewModel>(factory = SessionsViewModelFactory())

    HandleNavigatingToNewSession(viewModel.sessionIdToNavigateTo, navigateToSession)

    val viewState by viewModel.viewState.collectAsState(SessionsListState.Loading)

    Box(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (val state = viewState) {
            is SessionsListState.Loading -> CircularProgressIndicator(modifier.align(Alignment.Center))
            is SessionsListState.Success -> Sessions(
                state.sessions,
                viewModel.callbacks,
                navigateToSession,
                Modifier.padding(8.dp)
            )

            is SessionsListState.Refreshing -> {
                Sessions(
                    state.sessions,
                    viewModel.callbacks,
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

@Composable
fun HandleNavigatingToNewSession(
    sessionIdToNavigateTo: Flow<String>,
    navigateToSession: (String) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            // to ensure it doesn't get lost during, say, screen rotation
            withContext(Dispatchers.Main.immediate) {
                sessionIdToNavigateTo.collect(navigateToSession)
            }
        }
    }
}

@Composable
private fun Sessions(
    sessions: List<SessionOverviewUi>,
    callbacks: SessionsCallbacks,
    navigateToSession: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (sessions.isNotEmpty()) {
            LazyVerticalGrid(
                // TODO: more dynamic size
                columns = GridCells.FixedSize(175.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(sessions, key = { it.id }) { session ->
                    SessionBox(session, callbacks) { navigateToSession(session.id) }
                }
            }
        } else {
            NoSessions(Modifier.weight(1f))
        }
        SessionsOptions(callbacks, sessions.isNotEmpty())
    }
}

@Composable
private fun SessionBox(
    session: SessionOverviewUi,
    callbacks: SessionsCallbacks,
    modifier: Modifier = Modifier,
    navigateToSession: () -> Unit,
) {
    val (dialogIsOpen, setDialogIsOpen) = rememberSaveable { mutableStateOf(false) }

    val isUnnamed = session.name.isNullOrBlank()
    val title = if (isUnnamed) stringResource(R.string.untitled) else session.name!!
    val textStyle = if (isUnnamed) null else FontStyle.Italic

    Card(modifier.clickable(onClick = navigateToSession)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontStyle = textStyle,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { setDialogIsOpen(true) }) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_menu_delete),
                    contentDescription = stringResource(R.string.delete),
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }

    if (dialogIsOpen) {
        ConfirmationDialog(close = { setDialogIsOpen(false) }) {
            callbacks.delete(session.id)
        }
    }
}

@Composable
private fun SessionsOptions(
    callbacks: SessionsCallbacks,
    deleteEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val (dialogIsOpen, setDialogIsOpen) = rememberSaveable { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier.fillMaxWidth()
    ) {
        Button(onClick = callbacks::new) {
            Text(stringResource(R.string.new_session))
        }
        Button(
            onClick = { setDialogIsOpen(true) },
            enabled = deleteEnabled,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
        ) {
            Text(stringResource(R.string.delete_all))
        }
    }

    if (dialogIsOpen) {
        ConfirmationDialog(close = { setDialogIsOpen(false) }, action = callbacks::deleteAll)
    }
}

@Composable
private fun NoSessions(
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(R.string.no_sessions),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
            .padding(16.dp)
            .wrapContentSize()
    )
}

class SessionListProvider : PreviewParameterProvider<List<SessionOverviewUi>> {
    override val values = listOf(
        listOf(
            SessionOverviewUi(name = "Session 1"),
            SessionOverviewUi(name = "Session 2"),
            SessionOverviewUi(name = "Session 3"),
            SessionOverviewUi(name = "Session 4 long name"),
            SessionOverviewUi(name = "Session 5 longest name, so long it doesn't fit in the space"),
            SessionOverviewUi(name = "Session 6"),
            SessionOverviewUi(name = "Session 7"),
        ),
        listOf(),
    ).asSequence()
}

@Suppress("EmptyFunctionBlock")
private val dummyCallbacks = object : SessionsCallbacks {
    override fun new(name: String?) {}
    override fun delete(id: String) {}
    override fun deleteAll() {}
}

@Suppress("EmptyFunctionBlock")
@PreviewLightDark
@Composable
private fun SessionBoxPreview() {
    SpaceTimeTaggerTheme {
        SessionBox(
            SessionOverviewUi(name = "Session Preview"),
            dummyCallbacks,
            Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(8.dp)
        ) {}
    }
}

@Suppress("EmptyFunctionBlock")
@PreviewLightDark
@Preview(widthDp = 720, heightDp = 360)
@Composable
private fun SessionsPreview(
    @PreviewParameter(SessionListProvider::class) sessions: List<SessionOverviewUi>,
) {
    SpaceTimeTaggerTheme {
        Sessions(
            sessions,
            dummyCallbacks,
            {},
            Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(8.dp)
        )
    }
}
