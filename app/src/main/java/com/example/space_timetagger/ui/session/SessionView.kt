package com.example.space_timetagger.ui.session

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.space_timetagger.R
import com.example.space_timetagger.domain.model.SessionCallbacks
import com.example.space_timetagger.domain.model.TagModel
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SessionView(
    modifier: Modifier = Modifier,
) {
    val viewModel = viewModel<SessionViewModel>()

    val tags = viewModel.tags.collectAsState().value

    Session(tags, viewModel.callbacks, modifier.padding(8.dp))
}

@Composable
private fun Session(
    tags: List<TagModel>,
    callbacks: SessionCallbacks,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
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
    Card(modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Text(text = "#${index + 1}", fontWeight = FontWeight.W800)
            Text(text = tag.dateTime.format(DateTimeFormatter.ofPattern("MMM dd, HH:mm.ss")))
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { callbacks.deleteTag(tag) }) {
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
private fun SessionOptions(
    callbacks: SessionCallbacks,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier.fillMaxWidth()
    ) {
        Button(onClick = { callbacks.addTag() }) {
            Text(stringResource(R.string.add_tag))
        }
        Button(
            onClick = { callbacks.clearTags() },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
        ) {
            Text(stringResource(R.string.delete_all))
        }
    }
}

@Suppress("EmptyFunctionBlock")
private val dummyCallbacks = object : SessionCallbacks {
    override fun addTag() {}
    override fun deleteTag(tag: TagModel) {}
    override fun clearTags() {}

}

@Preview(showBackground = true, heightDp = 600)
@Composable
private fun SessionPreview() {
    SpaceTimeTaggerTheme {
        Session(
            listOf(
                TagModel(OffsetDateTime.now().minusMinutes(8)),
                TagModel(OffsetDateTime.now().minusMinutes(5)),
                TagModel(OffsetDateTime.now().minusSeconds(23)),
            ),
            dummyCallbacks,
            Modifier.padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TagPreview() {
    SpaceTimeTaggerTheme {
        Tag(
            index = 1,
            tag = TagModel(OffsetDateTime.now().minusSeconds(23)),
            dummyCallbacks,
            Modifier.padding(8.dp)
        )
    }
}