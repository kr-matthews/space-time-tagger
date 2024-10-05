package com.example.space_timetagger.ui.session

import android.content.res.Configuration
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.space_timetagger.R
import com.example.space_timetagger.domain.model.SessionCallbacks
import com.example.space_timetagger.domain.model.TagModel
import com.example.space_timetagger.ui.common.ConfirmationDialog
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SessionView(
    id: String,
    modifier: Modifier = Modifier,
) {
    val viewModel = viewModel<SessionViewModel>(key = id)

    val name = viewModel.name.collectAsState().value
    val tags = viewModel.tags.collectAsState().value

    Session(
        name,
        tags,
        viewModel.callbacks,
        modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp)
    )
}

@Composable
private fun Session(
    name: String?,
    tags: List<TagModel>,
    callbacks: SessionCallbacks,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Header(name, callbacks::setName)
        if (tags.isNotEmpty()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(tags, key = { _, item -> item.dateTime }) { index, tag ->
                    Tag(index, tag, callbacks)
                }
            }
        } else {
            NoTags(Modifier.weight(1f))
        }
        SessionOptions(callbacks, tags.isNotEmpty())
    }
}

@Composable
private fun Header(
    name: String?,
    setName: (String?) -> Unit,
) {
    val title = if (name.isNullOrBlank()) stringResource(R.string.tap_to_add_title) else name
    val textStyle = if (name.isNullOrBlank()) FontStyle.Italic else null

    val (editModeIsOn, setEditModeIsOn) = rememberSaveable { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    // unfortunately, the cursor will go to the front of the input, not the end
    LaunchedEffect(editModeIsOn) {
        if (editModeIsOn) {
            focusRequester.requestFocus()
        }
    }

    if (editModeIsOn) {
        TextField(
            value = name ?: "",
            onValueChange = setName,
            placeholder = { stringResource(R.string.untitled) },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = {
                setName(name?.trim())
                setEditModeIsOn(false)
            }),
            modifier = Modifier.focusRequester(focusRequester),
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
                .clickable { setEditModeIsOn(true) }
                .padding(8.dp)
        )
    }
}

@Composable
private fun Tag(
    index: Int,
    tag: TagModel,
    callbacks: SessionCallbacks,
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
            Text(text = tag.dateTime.format(DateTimeFormatter.ofPattern("MMM dd, HH:mm.ss")))
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
        ConfirmationDialog(close = { setDialogIsOpen(false) }) {
            callbacks.deleteTag(tag)
        }
    }
}

@Composable
private fun SessionOptions(
    callbacks: SessionCallbacks,
    deleteEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val (dialogIsOpen, setDialogIsOpen) = rememberSaveable { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier.fillMaxWidth()
    ) {
        Button(onClick = callbacks::addTag) {
            Text(stringResource(R.string.add_tag))
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
        ConfirmationDialog(close = { setDialogIsOpen(false) }, action = callbacks::clearTags)
    }
}

@Composable
private fun NoTags(
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(R.string.no_tags),
        textAlign = TextAlign.Center,
        modifier = modifier
            .padding(16.dp)
            .wrapContentSize()
    )
}

class TagListProvider : PreviewParameterProvider<List<TagModel>> {
    override val values = listOf(
        listOf(),
        listOf(
            TagModel(OffsetDateTime.now().minusMinutes(8)),
            TagModel(OffsetDateTime.now().minusMinutes(5)),
            TagModel(OffsetDateTime.now().minusSeconds(23)),
        ),
    ).asSequence()
}

@Suppress("EmptyFunctionBlock")
private val dummyCallbacks = object : SessionCallbacks {
    override fun setName(name: String?) {}
    override fun addTag(now: OffsetDateTime) {}
    override fun deleteTag(tag: TagModel) {}
    override fun clearTags() {}

}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TagPreview() {
    SpaceTimeTaggerTheme {
        Tag(
            index = 1,
            tag = TagModel(OffsetDateTime.now().minusSeconds(23)),
            dummyCallbacks,
            Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(8.dp)
        )
    }
}

@Preview(showBackground = true, heightDp = 600)
@Preview(showBackground = true, heightDp = 600, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, heightDp = 300, widthDp = 600)
@Composable
private fun SessionPreview(
    @PreviewParameter(TagListProvider::class) tags: List<TagModel>,
) {
    SpaceTimeTaggerTheme {
        Session(
            "Preview Title",
            tags,
            dummyCallbacks,
            Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(8.dp)
        )
    }
}
