package com.example.space_timetagger.core.presentation

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.space_timetagger.R
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme


@Composable
fun TopBarSettingsIcon(
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(onClick = onTap, modifier = modifier) {
        Icon(
            painter = painterResource(android.R.drawable.ic_menu_preferences),
            contentDescription = stringResource(R.string.settings),
        )
    }
}

@MyPreview
@Composable
private fun TopBarSettingsIconPreview() {
    SpaceTimeTaggerTheme {
        TopBarSettingsIcon(onTap = {})
    }
}
