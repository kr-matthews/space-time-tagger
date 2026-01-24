package com.example.space_timetagger.sessions.presentation.sessionDetail

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.space_timetagger.R
import com.example.space_timetagger.core.presentation.EditTextDialog
import com.example.space_timetagger.core.presentation.Error
import com.example.space_timetagger.core.presentation.MyScaffold
import com.example.space_timetagger.core.presentation.MyTopBar
import com.example.space_timetagger.core.presentation.ScreenPreviews
import com.example.space_timetagger.core.presentation.TopBarSettingsIcon
import com.example.space_timetagger.sessions.presentation.models.SessionDetailUiModel
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme

@Composable
fun SessionDetailScreen(
    id: String,
    onBackTap: () -> Unit,
    onSettingsTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val viewModel = viewModel<SessionViewModel>(key = id, factory = SessionViewModelFactory(id))
    val viewState by viewModel.viewState.collectAsStateWithLifecycle(SessionDetailViewState.Loading)

    val tagBeingAddedToast = Toast.makeText(context, R.string.tag_being_added, Toast.LENGTH_SHORT)

    fun onEvent(event: SessionDetailEvent) {
        viewModel.handleEvent(event)
        when (event) {
            is SessionDetailEvent.TapBack -> onBackTap()
            is SessionDetailEvent.TapSettings -> onSettingsTap()
            // TODO: move into SessionDetailView and add UI tests?
            is SessionDetailEvent.TapNewTagButton -> tagBeingAddedToast.show()
            is SessionDetailEvent.TapAnywhere -> tagBeingAddedToast.show()
            else -> {}
        }
    }

    SessionDetailView(
        viewState = viewState,
        onEvent = ::onEvent,
        modifier = modifier
    )
}

@Composable
fun SessionDetailView(
    viewState: SessionDetailViewState,
    onEvent: (SessionDetailEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val session = (viewState as? SessionDetailViewState.Success)?.session
    if (session?.nameIsBeingEdited == true) {
        EditTextDialog(
            title = stringResource(R.string.session_name),
            initialText = session.name,
            onConfirm = { onEvent(SessionDetailEvent.ConfirmNameEdit(it)) },
            onCancel = { onEvent(SessionDetailEvent.CancelNameEdit) },
        )
    }

    val title = when (viewState) {
        is SessionDetailViewState.Success -> {
            viewState.session.name ?: stringResource(R.string.untitled)
        }

        is SessionDetailViewState.Refreshing -> {
            viewState.session.name ?: stringResource(R.string.untitled)
        }

        is SessionDetailViewState.Error,
        is SessionDetailViewState.Loading -> stringResource(R.string.session_detail)
    }

    MyScaffold(
        topBar = {
            SessionDetailTopBar(
                title = title,
                onBackTap = { onEvent(SessionDetailEvent.TapBack) },
                withOptions = viewState is SessionDetailViewState.Success,
                onRenameTap = { onEvent(SessionDetailEvent.TapRename) },
                onTimeOffsetTap = { onEvent(SessionDetailEvent.TapTimeOffset) },
                onDeleteTap = { onEvent(SessionDetailEvent.TapDelete) },
                onSettingsTap = { onEvent(SessionDetailEvent.TapSettings) },
            )
        },
        modifier = modifier,
    ) {
        Box(
            it
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (viewState) {
                is SessionDetailViewState.Loading -> CircularProgressIndicator(
                    Modifier.align(Alignment.Center)
                )

                is SessionDetailViewState.Success -> SessionDetail(
                    session = viewState.session,
                    onEvent = onEvent,
                    modifier = Modifier.padding(8.dp)
                )

                is SessionDetailViewState.Refreshing -> {
                    SessionDetail(
                        session = viewState.session,
                        onEvent = onEvent,
                        modifier = Modifier.padding(8.dp)
                    )
                    CircularProgressIndicator(modifier.align(Alignment.Center))
                }

                SessionDetailViewState.Error -> Error(
                    stringResource(R.string.error_session_detail),
                    Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun SessionDetailTopBar(
    title: String,
    onBackTap: () -> Unit,
    withOptions: Boolean,
    onRenameTap: (() -> Unit)?,
    onTimeOffsetTap: () -> Unit,
    onDeleteTap: () -> Unit,
    onSettingsTap: () -> Unit,
) {
    val (isOpen, setIsOpen) = remember { mutableStateOf(false) }

    MyTopBar(
        title = title,
        onBackTap = onBackTap,
    ) {
        if (withOptions) {
            Image(
                painter = painterResource(R.drawable.ic_arrow_dropdown),
                contentDescription = stringResource(R.string.dropdown_icon),
                modifier = Modifier.clickable { setIsOpen(true) }
            )
            DropdownMenu(
                expanded = isOpen,
                onDismissRequest = { setIsOpen(false) },
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.rename)) },
                    leadingIcon = {
                        Image(
                            painter = painterResource(R.drawable.ic_pencil),
                            contentDescription = stringResource(R.string.rename),
                        )
                    },
                    onClick = {
                        onRenameTap?.invoke()
                        setIsOpen(false)
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.time_offset)) },
                    leadingIcon = {
                        Image(
                            painter = painterResource(R.drawable.ic_clock_plus),
                            contentDescription = stringResource(R.string.time_offset),
                        )
                    },
                    onClick = {
                        onTimeOffsetTap()
                        setIsOpen(false)
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.delete)) },
                    leadingIcon = {
                        Image(
                            painter = painterResource(R.drawable.ic_trash_x),
                            contentDescription = stringResource(R.string.delete),
                        )
                    },
                    onClick = {
                        onDeleteTap()
                        setIsOpen(false)
                    }
                )
            }
        }
        TopBarSettingsIcon(onTap = onSettingsTap)
    }
}

class ViewStateProvider : PreviewParameterProvider<SessionDetailViewState> {
    override val values = sequenceOf(
        SessionDetailViewState.Success(
            SessionDetailUiModel(
                id = "id",
                name = "Shopping Trip",
                nameIsBeingEdited = false,
                tags = someTags,
                tagIdToScrollTo = someTags.last().id,
                deleteAllIsEnabled = true,
                tapAnywhereIsEnabled = false,
                progress = with(someTags) { if (isEmpty()) null else count { it.isArchived } / size.toFloat() }
            ),
        ),
        SessionDetailViewState.Success(
            SessionDetailUiModel(
                id = "id",
                name = "Session with no tags",
                nameIsBeingEdited = false,
                tags = noTags,
                tagIdToScrollTo = null,
                deleteAllIsEnabled = false,
                tapAnywhereIsEnabled = false,
                progress = with(noTags) { if (isEmpty()) null else count { it.isArchived } / size.toFloat() }
            ),
        ),
        SessionDetailViewState.Success(
            SessionDetailUiModel(
                id = "id",
                name = null,
                nameIsBeingEdited = false,
                tags = manyTags,
                tagIdToScrollTo = manyTags.last().id,
                deleteAllIsEnabled = true,
                tapAnywhereIsEnabled = true,
                progress = with(manyTags) { if (isEmpty()) null else count { it.isArchived } / size.toFloat() }
            ),
        ),
        SessionDetailViewState.Loading,
        SessionDetailViewState.Error,
        SessionDetailViewState.Refreshing(
            SessionDetailUiModel(
                id = "id",
                name = "Refreshing 10pm",
                nameIsBeingEdited = false,
                tags = someTags,
                tagIdToScrollTo = null,
                deleteAllIsEnabled = true,
                tapAnywhereIsEnabled = true,
                progress = with(someTags) { if (isEmpty()) null else count { it.isArchived } / size.toFloat() }
            ),
        ),
    )
}

@ScreenPreviews
@Composable
private fun SessionDetailPreview(
    @PreviewParameter(ViewStateProvider::class) viewState: SessionDetailViewState,
) {
    SpaceTimeTaggerTheme {
        SessionDetailView(viewState, {})
    }
}
