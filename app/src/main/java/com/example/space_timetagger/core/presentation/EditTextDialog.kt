package com.example.space_timetagger.core.presentation

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.window.Dialog
import com.example.space_timetagger.R

@Composable
fun EditTextDialog(
    initialText: String?,
    onConfirmClick: (newText: String) -> Unit,
    onCancelClick: () -> Unit
) {
    val (text, setText) = rememberSaveable { mutableStateOf(initialText ?: "") }
    val contentDescription = stringResource(R.string.name_input)
    val focusRequester = remember { FocusRequester() }

    // unfortunately, the cursor will go to the front of the input, not the end
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    // TODO: option to clear text
    // TODO: buttons (and background etc)
    Dialog(onDismissRequest = onCancelClick) {
        TextField(
            value = text,
            onValueChange = setText,
            placeholder = { stringResource(R.string.untitled) },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = { onConfirmClick(text) }),
            modifier = Modifier
                .focusRequester(focusRequester)
                .semantics { this.contentDescription = contentDescription }
        )
    }
}