package com.example.space_timetagger.sessions.presentation.sessionDetail

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.space_timetagger.R
import com.example.space_timetagger.core.presentation.ConfirmationDialog
import com.example.space_timetagger.core.presentation.formatShortDateLongTime
import com.example.space_timetagger.sessions.presentation.models.SessionDetailUiModel
import com.example.space_timetagger.sessions.presentation.models.TagUiModel
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme
import java.time.OffsetDateTime

@Composable
fun SessionDetail(
    session: SessionDetailUiModel,
    onEvent: (SessionDetailEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tags = session.tags

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = { onEvent(SessionDetailEvent.TapAnywhere(OffsetDateTime.now())) },
                enabled = session.tapAnywhereIsEnabled
            )
    ) {
        Header(
            name = session.name,
            editModeIsOn = session.nameIsBeingEdited,
            onTapName = { onEvent(SessionDetailEvent.TapName) },
            onTapNameDoneEditing = { onEvent(SessionDetailEvent.TapNameDoneEditing) },
            onNameChange = { onEvent(SessionDetailEvent.ChangeName(it)) },
        )
        if (tags.isNotEmpty()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(tags, key = { _, item -> item.dateTime }) { index, tag ->
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
private fun Header(
    name: String?,
    editModeIsOn: Boolean,
    onTapName: () -> Unit,
    onTapNameDoneEditing: () -> Unit,
    onNameChange: (String?) -> Unit,
) {
    val title = if (name.isNullOrBlank()) stringResource(R.string.tap_to_add_title) else name
    val textStyle = if (name.isNullOrBlank()) FontStyle.Italic else null

    val focusRequester = remember { FocusRequester() }

    val contentDescription = stringResource(R.string.name_input)

    // unfortunately, the cursor will go to the front of the input, not the end
    LaunchedEffect(editModeIsOn) {
        if (editModeIsOn) {
            focusRequester.requestFocus()
        }
    }

    if (editModeIsOn) {
        TextField(
            value = name ?: "",
            onValueChange = onNameChange,
            placeholder = { stringResource(R.string.untitled) },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = {
                onNameChange(name?.trim())
                onTapNameDoneEditing()
            }),
            modifier = Modifier
                .focusRequester(focusRequester)
                .semantics { this.contentDescription = contentDescription }
        )
    } else {
        Text(
            text = title,
            fontSize = 18.sp,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.W800,
            fontStyle = textStyle,
            modifier = Modifier
                .clickable(onClick = onTapName)
                .padding(8.dp)
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
    onTapAddNewTag: (OffsetDateTime) -> Unit,
    onTapConfirmDeleteAllTags: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (dialogIsOpen, setDialogIsOpen) = rememberSaveable { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier.fillMaxWidth()
    ) {
        Button(onClick = { onTapAddNewTag(OffsetDateTime.now()) }) {
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

@PreviewLightDark
@Composable
private fun TagPreview() {
    SpaceTimeTaggerTheme {
        Tag(
            index = 2,
            tag = tag,
            {},
            Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(8.dp)
        )
    }
}
