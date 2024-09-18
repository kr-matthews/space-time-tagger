package com.example.space_timetagger.ui.sessions

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.space_timetagger.R
import com.example.space_timetagger.domain.model.SessionModel
import com.example.space_timetagger.domain.model.SessionsCallbacks
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme
import java.util.UUID

@Composable
fun SessionsView(
    modifier: Modifier = Modifier,
) {
    val viewModel = viewModel<SessionsViewModel>()

    val sessions = viewModel.sessions.collectAsState().value

    Sessions(
        sessions,
        viewModel.callbacks,
        modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp)
    )
}

@Composable
private fun Sessions(
    sessions: List<SessionModel>,
    callbacks: SessionsCallbacks,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        LazyVerticalGrid(
            columns = GridCells.FixedSize(175.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(sessions, key = { it.id }) { session ->
                SessionBox(session, callbacks)
            }
        }
        SessionsOptions(callbacks)
    }
}

@Composable
private fun SessionBox(
    session: SessionModel,
    callbacks: SessionsCallbacks,
    modifier: Modifier = Modifier,
) {
    Card(modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { callbacks.select(session.id) }
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = session.name,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { callbacks.delete(session.id) }) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_menu_delete),
                    contentDescription = stringResource(R.string.delete),
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

@Composable
private fun SessionsOptions(
    callbacks: SessionsCallbacks,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier.fillMaxWidth()
    ) {
        Button(onClick = { callbacks.new() }) {
            Text(stringResource(R.string.new_session))
        }
        Button(
            onClick = { callbacks.clearAll() },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
        ) {
            Text(stringResource(R.string.delete_all))
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

@Preview(showBackground = true, heightDp = 600)
@Preview(showBackground = true, heightDp = 600, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, heightDp = 300, widthDp = 600)
@Composable
private fun SessionsPreview() {
    SpaceTimeTaggerTheme {
        Sessions(
            listOf(
                SessionModel("Session 1"),
                SessionModel("Session 2"),
                SessionModel("Session 3"),
                SessionModel("Session 4 long name"),
                SessionModel("Session 5 longest name, so long it doesn't fit"),
                SessionModel("Session 6"),
                SessionModel("Session 7"),
            ),
            dummyCallbacks,
            Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SessionBoxPreview() {
    SpaceTimeTaggerTheme {
        SessionBox(
            SessionModel("Session Preview"),
            dummyCallbacks,
            Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(8.dp)
        )
    }
}