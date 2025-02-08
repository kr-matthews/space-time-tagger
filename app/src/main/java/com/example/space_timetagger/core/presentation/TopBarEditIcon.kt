package com.example.space_timetagger.core.presentation

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.space_timetagger.R
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme


@Composable
fun TopBarEditIcon(
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(onClick = onTap, modifier = modifier) {
        Icon(
            painter = painterResource(android.R.drawable.ic_menu_edit),
            contentDescription = stringResource(R.string.edit),
        )
    }
}

@Preview
@Composable
private fun TopBarEditIconPreview() {
    SpaceTimeTaggerTheme {
        TopBarEditIcon(onTap = {})
    }
}
