package com.example.space_timetagger.ui.session

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.space_timetagger.R
import com.example.space_timetagger.domain.model.SessionCallbacks
import com.example.space_timetagger.domain.model.TagModel
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SessionView(
    modifier: Modifier = Modifier,
) {
    val viewModel = viewModel<SessionViewModel>()

    val tags = viewModel.tags.collectAsState().value

    Session(tags, viewModel.callbacks, modifier)
}

@Composable
private fun Session(
    tags: List<TagModel>,
    callbacks: SessionCallbacks,
    modifier: Modifier = Modifier,
) {
    Column {
        LazyColumn(modifier.weight(1f)) {
            itemsIndexed(tags, key = { _, item -> item.dateTime }) { index, tag ->
                Tag(index, tag, callbacks)
            }
        }
        SessionOptions(callbacks)
    }
}

@Composable
private fun Tag(
    index: Int,
    tag: TagModel,
    callbacks: SessionCallbacks,
    modifier: Modifier = Modifier,
) {
    Row(modifier) {
        Text(text = "#${index + 1}")
        Spacer(Modifier.width(8.dp))
        Text(text = tag.dateTime.format(DateTimeFormatter.ofPattern("MMM dd, HH:mm.ss")))
        Spacer(Modifier.weight(1f))
        IconButton(onClick = { callbacks.deleteTag(tag) }) {
            Icon(
                painter = painterResource(android.R.drawable.ic_menu_delete),
                contentDescription = stringResource(R.string.delete),
            )
        }
    }
}

@Composable
private fun SessionOptions(
    callbacks: SessionCallbacks,
) {
    Row {
        IconButton(onClick = { callbacks.addTag() }) {
            Icon(
                painter = painterResource(android.R.drawable.ic_input_add),
                contentDescription = stringResource(R.string.add_new),
            )
        }
        IconButton(onClick = { callbacks.clearTags() }) {
            Icon(
                painter = painterResource(android.R.drawable.ic_delete),
                contentDescription = stringResource(R.string.delete_all),
            )
        }
    }
}

@Suppress("EmptyFunctionBlock")
private val dummyCallbacks = object : SessionCallbacks {
    override fun addTag() {}
    override fun deleteTag(tag: TagModel) {}
    override fun clearTags() {}

}

@Preview(showBackground = true)
@Composable
private fun SessionPreview() {
    Session(
        listOf(
            TagModel(OffsetDateTime.now().minusMinutes(8)),
            TagModel(OffsetDateTime.now().minusMinutes(5)),
            TagModel(OffsetDateTime.now().minusSeconds(23)),
        ),
        dummyCallbacks,
    )
}

@Preview(showBackground = true)
@Composable
private fun TagPreview() {
    Tag(
        index = 1,
        tag = TagModel(OffsetDateTime.now().minusSeconds(23)),
        dummyCallbacks,
    )
}