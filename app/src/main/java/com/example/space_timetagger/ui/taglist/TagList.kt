package com.example.space_timetagger.ui.taglist

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun TagListView(
    modifier: Modifier = Modifier,
) {
    // temporary hard-coded list until ui exists to add new items
    val tagList = rememberSaveable {
        mutableListOf(
            TagModel(OffsetDateTime.now().minusHours(4)),
            TagModel(OffsetDateTime.now().minusMinutes(5)),
            TagModel(OffsetDateTime.now().minusSeconds(6)),
        )
    }

    TagList(items = tagList.toList(), modifier)
}

@Composable
private fun TagList(
    items: List<TagModel>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier) {
        itemsIndexed(items, key = { _, item -> item.dateTime }) { index, item ->
            TagItem(index, item)
        }
    }
}

@Composable
private fun TagItem(
    index: Int,
    item: TagModel,
    modifier: Modifier = Modifier,
) {
    Row(modifier) {
        Text(text = "${index + 1}.")
        Text(text = item.dateTime.format(DateTimeFormatter.ofPattern("MMM dd, HH:mm.ss")))
    }
}

@Preview
@Composable
private fun TagListPreview() {
    TagList(
        listOf(
            TagModel(OffsetDateTime.now().minusMinutes(8)),
            TagModel(OffsetDateTime.now().minusMinutes(5)),
            TagModel(OffsetDateTime.now().minusSeconds(23)),
        )
    )
}

@Preview
@Composable
private fun TagItemPreview() {
    TagItem(
        index = 1,
        item = TagModel(OffsetDateTime.now().minusSeconds(23)),
    )
}