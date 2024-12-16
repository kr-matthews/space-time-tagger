package com.example.space_timetagger.sessions.presentation.sessionDetail

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextReplacement
import androidx.test.platform.app.InstrumentationRegistry
import com.example.space_timetagger.R
import com.example.space_timetagger.core.presentation.formatShortDateLongTime
import com.example.space_timetagger.sessions.presentation.models.SessionDetailUiModel
import com.example.space_timetagger.sessions.presentation.models.TagUiModel
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import java.time.OffsetDateTime
import java.time.ZoneOffset

class SessionDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    private val mockHandleEvent: (SessionDetailEvent) -> Unit = mock()
    private val now: OffsetDateTime = OffsetDateTime.of(2024, 7, 19, 8, 33, 6, 5, ZoneOffset.UTC)
    private val session = SessionDetailUiModel(
        name = "Test session name",
        nameIsBeingEdited = false,
        tags = List(6) { i -> TagUiModel(dateTime = now.minusSeconds(i * i + 2L)) },
        deleteAllIsEnabled = true,
    )
    private val successState = SessionDetailViewState.Success(session)
    private val newSuccessState = SessionDetailViewState.Success(
        SessionDetailUiModel(
            name = null,
            nameIsBeingEdited = false,
            tags = listOf(),
            deleteAllIsEnabled = false,
        ),
    )
    private val loadingState = SessionDetailViewState.Loading
    private val errorState = SessionDetailViewState.Error
    private val refreshState = SessionDetailViewState.Refreshing(session)

    private fun setup(viewState: SessionDetailViewState) {
        composeTestRule.setContent {
            SpaceTimeTaggerTheme {
                SessionDetailView(viewState, mockHandleEvent)
            }
        }
    }

    // success

    @Test
    fun successState_doesNotHaveProgressIndicator() {
        setup(successState)
        getProgressIndicator().assertDoesNotExist()
    }

    @Test
    fun successState_doesNotHaveErrorMessage() {
        setup(successState)
        getErrorMessage().assertDoesNotExist()
    }

    @Test
    fun successState_showsName() {
        setup(successState)
        composeTestRule.onNodeWithText(successState.session.name!!).assertIsDisplayed()
    }

    @Test
    fun successState_canUpdateName() {
        setup(successState)
        val newName = "Banana"
        updateName(successState.session.name, newName)
        verify(mockHandleEvent, times(1)).invoke(SessionDetailEvent.ChangeName(newName))
    }

    @Test
    fun successState_showsTags() {
        setup(successState)
        assertShowsTags(successState.session.tags)
    }

    @Test
    fun successState_canDeleteATags() {
        setup(successState)
        val tagToDelete = successState.session.tags.let {
            val index = 2.coerceAtMost(it.size - 1)
            it[index]
        }
        deleteTag(tagToDelete)
        verify(
            mockHandleEvent,
            times(1)
        ).invoke(SessionDetailEvent.TapConfirmDeleteTag(tagToDelete.id))
    }

    @Test
    fun successState_hasAddTagButtonWhichCallsCallback() {
        setup(successState)
        assertAddTagButtonWorks()
    }

    @Test
    fun successState_hasDeleteTagsButtonWhichCallsCallback() {
        setup(successState)
        assertDeleteTagsButtonWorks()
    }

    // new session success (no name, empty tags)

    @Test
    fun newSuccessState_doesNotHaveProgressIndicator() {
        setup(newSuccessState)
        getProgressIndicator().assertDoesNotExist()
    }

    @Test
    fun newSuccessState_doesNotHaveErrorMessage() {
        setup(newSuccessState)
        getErrorMessage().assertDoesNotExist()
    }

    @Test
    fun newSuccessState_showsName() {
        setup(newSuccessState)
        composeTestRule.onNodeWithText(appContext.getString(R.string.tap_to_add_title))
            .assertIsDisplayed()
    }

    @Test
    fun newSuccessState_canUpdateName() {
        setup(newSuccessState)
        val newName = "Cherry"
        updateName(null, newName)
        verify(mockHandleEvent, times(1)).invoke(SessionDetailEvent.ChangeName(newName))
    }

    @Test
    fun newSuccessState_showsNoTagMessage() {
        setup(newSuccessState)
        composeTestRule.onNodeWithText(appContext.getString(R.string.no_tags)).assertIsDisplayed()
    }

    @Test
    fun newSuccessState_hasAddTagButtonWhichCallsCallback() {
        setup(newSuccessState)
        assertAddTagButtonWorks()
    }

    @Test
    fun newSuccessState_hasDisabledDeleteTagsButton() {
        setup(newSuccessState)
        assertDeleteTagsButtonDisabled()
    }

    // loading

    @Test
    fun successState_hasProgressIndicator() {
        setup(loadingState)
        getProgressIndicator().assertIsDisplayed()
    }

    // error

    @Test
    fun errorState_doesNotHaveProgressIndicator() {
        setup(errorState)
        getProgressIndicator().assertDoesNotExist()
    }

    @Test
    fun errorState_hasErrorMessage() {
        setup(errorState)
        getErrorMessage().assertIsDisplayed()
    }

    // refreshing

    @Test
    fun refreshingState_hasProgressIndicator() {
        setup(refreshState)
        getProgressIndicator().assertIsDisplayed()
    }

    @Test
    fun refreshingState_doesNotHaveErrorMessage() {
        setup(refreshState)
        getErrorMessage().assertDoesNotExist()
    }

    @Test
    fun refreshingState_showsTags() {
        setup(refreshState)
        assertShowsTags(refreshState.session.tags)
    }

    @Test
    fun refreshState_showsName() {
        setup(refreshState)
        composeTestRule.onNodeWithText(refreshState.session.name!!).assertIsDisplayed()
    }

    // more refresh state tests? buttons are disabled?

    // helpers

    private fun getProgressIndicator() =
        composeTestRule.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo))

    private fun getErrorMessage() =
        composeTestRule.onNodeWithText(appContext.getString(R.string.error_session_detail))

    private fun updateName(currentName: String?, newName: String) {
        val text = currentName ?: appContext.getString(R.string.tap_to_add_title)
        composeTestRule.onNodeWithText(text).apply {
            assertHasClickAction()
            performClick()
        }
        composeTestRule.onNodeWithContentDescription(appContext.getString(R.string.name_input))
            .apply {
                performTextReplacement(newName)
                performImeAction()
            }
    }

    private fun assertShowsTags(tags: List<TagUiModel>) {
        tags.forEach {
            composeTestRule.onNodeWithText(it.dateTime.formatShortDateLongTime())
                .assertIsDisplayed()
        }
    }

    private fun deleteTag(tag: TagUiModel) {
        composeTestRule.onNodeWithText(tag.dateTime.formatShortDateLongTime())
            .onParent()
            .onChildren()
            .filterToOne(hasContentDescription(appContext.getString(R.string.delete))).apply {
                assertHasClickAction()
                assertIsEnabled()
                performClick()
            }
        composeTestRule.onNodeWithText(appContext.getString(R.string.confirm)).performClick()
    }

    private fun assertAddTagButtonWorks() {
        composeTestRule.onNodeWithText(appContext.getString(R.string.add_tag)).apply {
            assertIsEnabled()
            performClick()
        }
        verify(mockHandleEvent, times(1)).invoke(SessionDetailEvent.TapNewTagButton(any()))
    }

    private fun assertDeleteTagsButtonWorks() {
        composeTestRule.onNodeWithText(appContext.getString(R.string.delete_all)).apply {
            assertIsEnabled()
            performClick()
        }
        composeTestRule.onNodeWithText(appContext.getString(R.string.confirm)).performClick()
        verify(mockHandleEvent, times(1)).invoke(SessionDetailEvent.TapConfirmDeleteAllTags)
    }

    private fun assertDeleteTagsButtonDisabled() {
        composeTestRule.onNodeWithText(appContext.getString(R.string.delete_all)).apply {
            assertHasClickAction()
            assertIsNotEnabled()
            performClick()
        }
        composeTestRule.onNodeWithText(appContext.getString(R.string.confirm)).assertDoesNotExist()
        verify(mockHandleEvent, times(0)).invoke(SessionDetailEvent.TapConfirmDeleteAllTags)
    }
}