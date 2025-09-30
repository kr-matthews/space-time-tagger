package com.example.space_timetagger.core.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.space_timetagger.R
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme

@Composable
fun ConfirmationDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onDismiss: (() -> Unit)? = null,
) {

    Dialog(onDismissRequest = onDismiss ?: onCancel) {
        Column(
            Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.are_you_sure),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(Modifier.weight(1f))
                Button(onClick = onConfirm) {
                    Text(stringResource(R.string.confirm))
                }
                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        }
    }
}

@ComponentPreviews
@Composable
private fun ConfirmationDialogPreview() {
    SpaceTimeTaggerTheme {
        ConfirmationDialog(onConfirm = {}, onCancel = {})
    }
}
