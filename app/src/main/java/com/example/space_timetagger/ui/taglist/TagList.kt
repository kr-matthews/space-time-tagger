package com.example.space_timetagger.ui.taglist

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun TagListView(
    modifier: Modifier = Modifier,
) {
    // temporary hard-coded list until ui exists to add new items
    val tagList = rememberSaveable {
        mutableListOf<LocalDateTime>(
            LocalDateTime.now().minusHours(4),
            LocalDateTime.now().minusMinutes(5),
            LocalDateTime.now().minusSeconds(6),
        )
    }

    TagList(items = tagList.toList(), modifier)
}

@Composable
private fun TagList(
    items: List<LocalDateTime>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier) {
        itemsIndexed(items, key = { _, item -> item }) { index, item ->
            TagItem(index, item)
        }
    }
}

@Composable
private fun TagItem(
    index: Int, item: LocalDateTime,
    modifier: Modifier = Modifier,
) {
    Row(modifier) {
        Text(text = "${index + 1}.")
        Text(text = item.format(DateTimeFormatter.ofPattern("MMM dd, HH:mm.ss")))
    }
}

@Preview
@Composable
private fun TagListPreview() {
    TagList(
        listOf(
            LocalDateTime.now().minusMinutes(8),
            LocalDateTime.now().minusMinutes(5),
            LocalDateTime.now().minusSeconds(23),
        )
    )
}

@Preview
@Composable
private fun TagItemPreview() {
    TagItem(
        index = 1,
        item = LocalDateTime.now().minusSeconds(23),
    )
}