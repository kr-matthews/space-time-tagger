package com.example.space_timetagger.sessions.presentation.sessionsList

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.space_timetagger.R
import com.example.space_timetagger.core.presentation.ConfirmationDialog
import com.example.space_timetagger.core.presentation.MyPreview
import com.example.space_timetagger.sessions.presentation.models.SessionOverviewUiModel
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme

@Composable
fun Sessions(
    sessions: List<SessionOverviewUiModel>,
    deleteAllIsEnabled: Boolean,
    onEvent: (SessionsListEvent) -> Unit,
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
                    SessionBox(
                        session = session,
                        onTap = { onEvent(SessionsListEvent.TapSession(session.id)) },
                        onTapConfirmDelete = {
                            onEvent(SessionsListEvent.TapConfirmDeleteSession(session.id))
                        },
                    )
                }
            }
        } else {
            NoSessions(Modifier.weight(1f))
        }
        SessionsOptions(
            onNewSessionTap = { onEvent(SessionsListEvent.TapNewSessionButton) },
            onConfirmDeleteAllSessions = { onEvent(SessionsListEvent.TapConfirmDeleteAllSessions) },
            deleteAllIsEnabled = deleteAllIsEnabled,
        )
    }
}

@Composable
private fun SessionBox(
    session: SessionOverviewUiModel,
    onTap: () -> Unit,
    onTapConfirmDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (dialogIsOpen, setDialogIsOpen) = rememberSaveable { mutableStateOf(false) }

    val isUnnamed = session.name.isNullOrBlank()
    val title = if (isUnnamed) stringResource(R.string.untitled) else session.name!!
    val textStyle = if (isUnnamed) FontStyle.Italic else null

    Card(modifier.clickable(onClick = onTap)) {
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
        ConfirmationDialog(
            onDismissRequest = { setDialogIsOpen(false) },
            action = onTapConfirmDelete,
        )
    }
}

@Composable
private fun SessionsOptions(
    onNewSessionTap: () -> Unit,
    onConfirmDeleteAllSessions: () -> Unit,
    deleteAllIsEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val (dialogIsOpen, setDialogIsOpen) = rememberSaveable { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier.fillMaxWidth()
    ) {
        Button(onClick = onNewSessionTap) {
            Text(stringResource(R.string.new_session))
        }
        Button(
            onClick = { setDialogIsOpen(true) },
            enabled = deleteAllIsEnabled,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
        ) {
            Text(stringResource(R.string.delete_all))
        }
    }

    if (dialogIsOpen) {
        ConfirmationDialog(
            onDismissRequest = { setDialogIsOpen(false) },
            action = onConfirmDeleteAllSessions,
        )
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

@MyPreview
@Composable
private fun SessionBoxPreview() {
    SpaceTimeTaggerTheme {
        SessionBox(
            SessionOverviewUiModel(name = "Session Preview"),
            {},
            {},
            Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(8.dp)
        )
    }
}
