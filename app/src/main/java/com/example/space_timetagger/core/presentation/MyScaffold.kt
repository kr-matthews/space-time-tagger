package com.example.space_timetagger.core.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme

@Composable
fun MyScaffold(
    topBar: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit,
) {
    Scaffold(
        topBar = topBar,
        modifier = modifier
    ) { paddingValues ->
        content(Modifier.padding(paddingValues))
    }
}

@MyPreview
@Composable
private fun MyScaffoldPreview() {
    SpaceTimeTaggerTheme {
        MyScaffold(
            topBar = { MyTopBarPreview() },
        ) {
            Text(text = "Preview content", modifier = it)
        }
    }
}
