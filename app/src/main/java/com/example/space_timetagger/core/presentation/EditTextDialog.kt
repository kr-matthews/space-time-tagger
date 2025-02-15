package com.example.space_timetagger.core.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.space_timetagger.R
import kotlinx.coroutines.delay

@Composable
fun EditTextDialog(
    title: String,
    initialText: String?,
    onConfirmClick: (newText: String) -> Unit,
    onCancelClick: () -> Unit
) {
    val (text, setText) = rememberSaveable { mutableStateOf(initialText ?: "") }
    val contentDescription = stringResource(R.string.name_input)
    val focusRequester = remember { FocusRequester() }

    // unfortunately, the cursor will go to the front of the input, not the end
    LaunchedEffect(Unit) {
        // without a delay, ui tests complain that the focus requester hasn't been initialized
        delay(10)
        focusRequester.requestFocus()
    }

    Dialog(onDismissRequest = onCancelClick) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 24.sp,
            )
            TextField(
                value = text,
                onValueChange = setText,
                placeholder = { stringResource(R.string.untitled) },
                trailingIcon = {
                    IconButton(
                        onClick = { setText("") }
                    ) {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                            contentDescription = stringResource(R.string.clear_text),
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = { onConfirmClick(text) }),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .semantics { this.contentDescription = contentDescription }
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(Modifier.weight(1f))
                Button(onClick = { onConfirmClick(text) }) {
                    Text(stringResource(R.string.confirm))
                }
                Button(
                    onClick = onCancelClick,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        }
    }
}

@PreviewLightDark
@Preview(device = Devices.TABLET)
@Composable
private fun EditTextDialogPreview() {
    EditTextDialog(
        title = "Fruit",
        initialText = "Pineapple",
        onConfirmClick = {},
        onCancelClick = {},
    )
}
