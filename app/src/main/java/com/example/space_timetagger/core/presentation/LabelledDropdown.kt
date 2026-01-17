package com.example.space_timetagger.core.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.space_timetagger.R
import com.example.space_timetagger.sessions.domain.models.SessionNameStrategy
import com.example.space_timetagger.sessions.domain.models.defaultSessionNameStrategy
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme
import kotlin.enums.EnumEntries

@Composable
fun <T : Enum<T>> LabelledDropdown(
    label: String,
    display: String,
    options: EnumEntries<T>,
    onTapOption: (option: T) -> Unit,
    modifier: Modifier = Modifier,
    // just for preview
    initialIsOpen: Boolean = false,
) {
    val (isOpen, setIsOpen) = remember { mutableStateOf(initialIsOpen) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clickable { setIsOpen(true) }
    ) {
        Text(text = label)
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(
                Modifier
                    .weight(1f)
            )
            Text(text = display)
            Box(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_arrow_dropdown),
                    contentDescription = stringResource(R.string.dropdown_icon),
                )
                DropdownMenu(
                    expanded = isOpen,
                    onDismissRequest = { setIsOpen(false) },
                ) {
                    options.map { option ->
                        DropdownMenuItem(
                            text = { Text(option.toString()) },
                            onClick = {
                                onTapOption(option)
                                setIsOpen(false)
                            }
                        )
                    }
                }
            }
        }

    }
}

@ComponentPreviews
@Composable
private fun LabelledDropdownPreview() {
    SpaceTimeTaggerTheme {
        LabelledDropdown(
            "Custom Setting",
            defaultSessionNameStrategy.toString(),
            SessionNameStrategy.entries,
            {},
            initialIsOpen = true,
        )
    }
}
