package com.example.space_timetagger.sessions.presentation.sessionDetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.space_timetagger.R
import com.example.space_timetagger.core.presentation.ComponentPreviews
import com.example.space_timetagger.core.presentation.ConfirmationDialog
import com.example.space_timetagger.core.presentation.formatShortDateLongTime
import com.example.space_timetagger.core.presentation.thenIf
import com.example.space_timetagger.sessions.presentation.models.SessionDetailUiModel
import com.example.space_timetagger.sessions.presentation.models.TagUiModel
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme
import java.time.LocalDateTime

@Composable
fun SessionDetail(
    session: SessionDetailUiModel,
    onEvent: (SessionDetailEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyColumnState = rememberLazyListState()

    LaunchedEffect(key1 = session.tagIdToScrollTo) {
        session.tagIdToScrollTo?.let { tagId ->
            val tagIndex = session.tags.indexOfFirst { it.id === tagId }
            if (tagIndex != -1) {
                lazyColumnState.animateScrollToItem(tagIndex)
                onEvent(SessionDetailEvent.AutoScrollToTag(tagId))
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            // always adding .clickable, but with enabled = session.tapAnywhereIsEnabled,
            // will make the delete-a-tag test fail for unknown reasons
            .thenIf(
                condition = session.tapAnywhereIsEnabled,
                ifTrue = {
                    clickable(
                        onClick = { onEvent(SessionDetailEvent.TapAnywhere(LocalDateTime.now())) },
                    )
                },
            )
    ) {
        if (session.tags.isNotEmpty()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                state = lazyColumnState,
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(session.tags, key = { _, item -> item.dateTime }) { index, tag ->
                    Tag(
                        index = index,
                        tag = tag,
                        onTapConfirmDelete = { onEvent(SessionDetailEvent.TapConfirmDeleteTag(tag.id)) },
                    )
                }
            }
        } else {
            NoTags(
                tapAnywhereIsEnabled = session.tapAnywhereIsEnabled,
                modifier = Modifier.weight(1f),
            )
        }
        SessionOptions(
            deleteAllIsEnabled = session.deleteAllIsEnabled,
            onTapAddNewTag = { onEvent(SessionDetailEvent.TapNewTagButton(it)) },
            onTapConfirmDeleteAllTags = { onEvent(SessionDetailEvent.TapConfirmDeleteAllTags) },
        )
    }
}

@Composable
private fun Tag(
    index: Int,
    tag: TagUiModel,
    onTapConfirmDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (dialogIsOpen, setDialogIsOpen) = rememberSaveable { mutableStateOf(false) }

    Card(modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Text(text = "#${index + 1}", fontWeight = FontWeight.W800)
            Text(text = tag.dateTime.formatShortDateLongTime())
            Spacer(Modifier.weight(1f))
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
private fun SessionOptions(
    deleteAllIsEnabled: Boolean,
    onTapAddNewTag: (LocalDateTime) -> Unit,
    onTapConfirmDeleteAllTags: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (dialogIsOpen, setDialogIsOpen) = rememberSaveable { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier.fillMaxWidth()
    ) {
        Button(onClick = { onTapAddNewTag(LocalDateTime.now()) }) {
            Text(stringResource(R.string.add_tag))
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
            action = onTapConfirmDeleteAllTags,
        )
    }
}

@Composable
private fun NoTags(
    tapAnywhereIsEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val textRes =
        if (tapAnywhereIsEnabled) R.string.no_tags_tap_anywhere else R.string.no_tags_tap_below

    Text(
        text = stringResource(textRes),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
            .padding(16.dp)
            .wrapContentSize()
    )
}

@ComponentPreviews
@Composable
private fun TagPreview() {
    SpaceTimeTaggerTheme {
        Tag(
            index = 2,
            tag = tag,
            {},
        )
    }
}
