package com.example.space_timetagger.core.presentation

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.space_timetagger.R
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme

@Composable
fun Error(
    @StringRes textId: Int,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(textId),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.error,
        modifier = modifier
            .padding(16.dp)
            .wrapContentSize()
    )
}

@PreviewLightDark
@Composable
private fun ErrorPreview() {
    SpaceTimeTaggerTheme {
        Error(
            R.string.could_not_load_sessions,
            Modifier.background(MaterialTheme.colorScheme.background)
        )
    }
}