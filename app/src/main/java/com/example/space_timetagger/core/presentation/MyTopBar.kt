package com.example.space_timetagger.core.presentation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.example.space_timetagger.R
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBackTap: (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit) = {}
) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            onBackTap?.let {
                IconButton(onClick = it) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }
        },
        actions = actions,
        modifier = modifier,
    )
}

@PreviewLightDark
@Composable
fun MyTopBarPreview() {
    SpaceTimeTaggerTheme {
        MyTopBar(
            title = "Preview title",
            onBackTap = {},
        ) {
            IconButton(onClick = {}) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_menu_preferences),
                    contentDescription = "",
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_menu_help),
                    contentDescription = "",
                )
            }
        }
    }
}
