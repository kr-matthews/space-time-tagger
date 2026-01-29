package com.example.space_timetagger.core.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.space_timetagger.R
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun EditDurationDialog(
    title: String,
    initialDuration: Duration?,
    onConfirm: (newDuration: Duration) -> Unit,
    onCancel: () -> Unit,
    onDismiss: (() -> Unit)? = null,
) {
    val absoluteInitialDuration = initialDuration?.absoluteValue ?: Duration.ZERO

    val (isNegative, setIsNegative) = rememberSaveable {
        mutableStateOf(initialDuration?.isNegative() ?: false)
    }
    val (hours, setHours) = rememberSaveable {
        mutableStateOf(absoluteInitialDuration.inWholeHours.toString().omit0())
    }
    val (minutes, setMinutes) = rememberSaveable {
        mutableStateOf(
            absoluteInitialDuration.minus(absoluteInitialDuration.inWholeHours.hours)
                .inWholeMinutes
                .toString()
                .omit0()
        )
    }
    val (seconds, setSeconds) = rememberSaveable {
        mutableStateOf(
            absoluteInitialDuration.minus(absoluteInitialDuration.inWholeMinutes.minutes)
                .inWholeSeconds.toString()
                .omit0()
        )
    }

    val absoluteDuration =
        hours.toLong().hours + minutes.toLong().minutes + seconds.toLong().seconds
    val duration = if (isNegative) -absoluteDuration else absoluteDuration

    val focusRequester = remember { FocusRequester() }

    // unfortunately, the cursor will go to the front of the input, not the end
    LaunchedEffect(Unit) {
        // without a delay, ui tests complain that the focus requester hasn't been initialized
        delay(10)
        focusRequester.requestFocus()
    }

    Dialog(onDismissRequest = onDismiss ?: onCancel) {
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PlusOrMinusInput(
                    isPositive = !isNegative,
                    onChange = { setIsNegative(!it) },
                )
                NumberInput(
                    label = stringResource(R.string.hours_abbr),
                    value = hours,
                    onChange = setHours,
                    onConfirm = { onConfirm(duration) },
                )
                NumberInput(
                    label = stringResource(R.string.minutes_abbr),
                    value = minutes,
                    onChange = setMinutes,
                    onConfirm = { onConfirm(duration) },
                )
                NumberInput(
                    label = stringResource(R.string.seconds_abbr),
                    value = seconds,
                    onChange = setSeconds,
                    onConfirm = { onConfirm(duration) },
                    focusRequester = focusRequester,
                )
                IconButton(
                    onClick = {
                        setHours("")
                        setMinutes("")
                        setSeconds("")
                        setIsNegative(false)
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_x_circled),
                        contentDescription = stringResource(R.string.clear_duration),
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(Modifier.weight(1f))
                Button(onClick = { onConfirm(duration) }) {
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

// could move to own file
@Composable
private fun NumberInput(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    onConfirm: () -> Unit,
    focusRequester: FocusRequester? = null,
    contentDescription: String? = null,
) {
    TextField(
        label = { Text(label, fontSize = 14.sp) },
        value = value,
        onValueChange = { onChange(it.filter { ch -> ch.isDigit() }) },
        placeholder = { Text("0") },
        maxLines = 1,
        textStyle = TextStyle(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(onDone = { onConfirm() }),
        modifier = Modifier
            .width(62.dp)
            .thenIf(focusRequester != null, { focusRequester(focusRequester!!) })
            .semantics { this.contentDescription = contentDescription ?: label }
    )
}

// TODO: make the UI nicer
@Composable
private fun PlusOrMinusInput(
    isPositive: Boolean,
    onChange: (isPositive: Boolean) -> Unit,
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioButton(selected = isPositive, onClick = { onChange(true) })
            Text("+")
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioButton(selected = !isPositive, onClick = { onChange(false) })
            Text("-")
        }
    }
}

private fun String.toLong() = filter { ch -> ch.isDigit() }.toLongOrNull() ?: 0

private fun String.omit0() = if (this == "0") "" else this

@ComponentPreviews
@Composable
private fun EditDurationDialogPreview() {
    SpaceTimeTaggerTheme {
        EditDurationDialog(
            title = "How long?",
            initialDuration = -(297.hours + 0.minutes + 54.seconds),
            onConfirm = {},
            onCancel = {},
        )
    }
}
