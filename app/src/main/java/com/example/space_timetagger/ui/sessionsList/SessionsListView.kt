package com.example.space_timetagger.ui.sessionsList

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.space_timetagger.R
import com.example.space_timetagger.domain.models.SessionsCallbacks
import com.example.space_timetagger.ui.common.ConfirmationDialog
import com.example.space_timetagger.ui.models.SessionOverviewUi
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme

@Composable
fun SessionsListView(
    modifier: Modifier = Modifier,
    navigateToSession: (String) -> Unit,
) {
    val viewModel = viewModel<SessionsViewModel>(factory = SessionsViewModelFactory())

    val sessions by viewModel.sessions.collectAsState(listOf())
    val sessionIdToNavigateTo by viewModel.sessionIdToNavigateTo.collectAsState()

    LaunchedEffect(sessionIdToNavigateTo) {
        sessionIdToNavigateTo?.let { id ->
            navigateToSession(id)
            viewModel.clearSessionIdToNavigateTo()
        }
    }

    Sessions(
        sessions,
        viewModel.callbacks,
        navigateToSession,
        modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp)
    )
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
                columns = GridCells.FixedSize(175.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
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
        modifier = modifier
            .padding(16.dp)
            .wrapContentSize()
    )
}

class SessionListProvider : PreviewParameterProvider<List<SessionOverviewUi>> {
    override val values = listOf(
        listOf(),
        listOf(
            SessionOverviewUi(name = "Session 1"),
            SessionOverviewUi(name = "Session 2"),
            SessionOverviewUi(name = "Session 3"),
            SessionOverviewUi(name = "Session 4 long name"),
            SessionOverviewUi(name = "Session 5 longest name, so long it doesn't fit in the space"),
            SessionOverviewUi(name = "Session 6"),
            SessionOverviewUi(name = "Session 7"),
        ),
    ).asSequence()
}

@Suppress("EmptyFunctionBlock")
private val dummyCallbacks = object : SessionsCallbacks {
    override fun new(name: String?) {}
    override fun delete(id: String) {}
    override fun deleteAll() {}
}

@Suppress("EmptyFunctionBlock")
@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
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
@Preview(showBackground = true, heightDp = 600)
@Preview(showBackground = true, heightDp = 600, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, heightDp = 300, widthDp = 600)
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