package com.example.space_timetagger.ui.sessions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.space_timetagger.R
import com.example.space_timetagger.domain.model.SessionModel
import com.example.space_timetagger.domain.model.SessionsCallbacks
import java.util.UUID

@Composable
fun SessionsView(
    modifier: Modifier = Modifier,
) {
    val viewModel = viewModel<SessionsViewModel>()

    val sessions = viewModel.sessions.collectAsState().value
    val selectedSessionId = viewModel.selectedSessionId.collectAsState().value

    Sessions(sessions, selectedSessionId, viewModel.callbacks, modifier)
}

@Composable
private fun Sessions(
    sessions: List<SessionModel>,
    selectedSessionId: UUID?,
    callbacks: SessionsCallbacks,
    modifier: Modifier = Modifier,
) {
    Column {
        LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = modifier.weight(1f)) {
            items(sessions, key = { it.id }) { session ->
                SessionBox(session, callbacks, isSelected = session.id == selectedSessionId)
            }
        }
        SessionsOptions(callbacks)
    }
}

// TODO: isSelected is temporary
@Composable
private fun SessionBox(
    session: SessionModel,
    callbacks: SessionsCallbacks,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .background(if (isSelected) Color.Yellow else Color.Gray)
            .clickable { callbacks.select(session.id) }
    ) {
        Text(text = session.name)
        IconButton(onClick = { callbacks.delete(session.id) }) {
            Icon(
                painter = painterResource(android.R.drawable.ic_menu_delete),
                contentDescription = stringResource(R.string.delete),
            )
        }
    }
}

@Composable
private fun SessionsOptions(
    callbacks: SessionsCallbacks,
) {
    Row {
        IconButton(onClick = { callbacks.new() }) {
            Icon(
                painter = painterResource(android.R.drawable.ic_input_add),
                contentDescription = stringResource(R.string.word_new),
            )
        }
        IconButton(onClick = { callbacks.clearAll() }) {
            Icon(
                painter = painterResource(android.R.drawable.ic_delete),
                contentDescription = stringResource(R.string.delete_all),
            )
        }
    }
}

@Suppress("EmptyFunctionBlock")
private val dummyCallbacks = object : SessionsCallbacks {
    override fun new(name: String?) {}
    override fun select(id: UUID?) {}
    override fun delete(id: UUID) {}
    override fun clearAll() {}
}

@Preview(showBackground = true)
@Composable
private fun SessionsPreview() {
    val selectedSession = SessionModel("Session 2")
    Sessions(
        listOf(
            SessionModel("Session 1"),
            selectedSession,
            SessionModel("Session 3"),
            SessionModel("Session 4"),
            SessionModel("Session 5"),
            SessionModel("Session 6"),
            SessionModel("Session 7"),
        ),
        selectedSession.id,
        dummyCallbacks,
    )
}

@Preview(showBackground = true)
@Composable
private fun SessionBoxPreview() {
    SessionBox(
        SessionModel("Session Preview"),
        dummyCallbacks,
        false,
    )
}