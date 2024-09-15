package com.example.space_timetagger.ui.taglist

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.space_timetagger.R
import com.example.space_timetagger.domain.model.TagListCallbacks
import com.example.space_timetagger.domain.model.TagModel
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun TagListView(
    modifier: Modifier = Modifier,
) {
    val tagList = remember { mutableStateListOf<TagModel>() }

    val callbacks = object : TagListCallbacks {
        override fun addTag(tag: TagModel) {
            Log.d("TagList", "adding ${tag.dateTime}")
            tagList.add(tag)
        }

        override fun deleteTag(tag: TagModel) {
            Log.d("TagList", "deleting ${tag.dateTime}")
            tagList.remove(tag)
        }

        override fun deleteAllTags() {
            Log.d("TagList", "deleting all")
            tagList.clear()
        }

    }

    TagList(tags = tagList.toList(), callbacks, modifier)
}

@Composable
private fun TagList(
    tags: List<TagModel>,
    callbacks: TagListCallbacks,
    modifier: Modifier = Modifier,
) {
    Column {
        LazyColumn(modifier) {
            itemsIndexed(tags, key = { _, item -> item.dateTime }) { index, tag ->
                Tag(index, tag, callbacks)
            }
        }
        TagListOptions(callbacks)
    }
}

@Composable
private fun Tag(
    index: Int,
    tag: TagModel,
    callbacks: TagListCallbacks,
    modifier: Modifier = Modifier,
) {
    Row(modifier) {
        Text(text = "${index + 1}.")
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
private fun TagListOptions(
    callbacks: TagListCallbacks,
) {
    Row {
        IconButton(onClick = { callbacks.addTag(TagModel(OffsetDateTime.now())) }) {
            Icon(
                painter = painterResource(android.R.drawable.ic_input_add),
                contentDescription = stringResource(R.string.delete),
            )
        }
        IconButton(onClick = { callbacks.deleteAllTags() }) {
            Icon(
                painter = painterResource(android.R.drawable.ic_delete),
                contentDescription = stringResource(R.string.delete_all),
            )
        }
    }
}

@Suppress("EmptyFunctionBlock")
private val dummyCallbacks = object : TagListCallbacks {
    override fun addTag(tag: TagModel) {}
    override fun deleteTag(tag: TagModel) {}
    override fun deleteAllTags() {}

}

@Preview(showBackground = true)
@Composable
private fun TagListPreview() {
    TagList(
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