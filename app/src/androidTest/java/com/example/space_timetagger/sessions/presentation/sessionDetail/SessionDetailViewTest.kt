package com.example.space_timetagger.sessions.presentation.sessionDetail

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
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
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import java.time.OffsetDateTime
import java.time.ZoneOffset

class SessionDetailViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    private val mockHandleEvent: (SessionDetailEvent) -> Unit = mock()
    private val now: OffsetDateTime = OffsetDateTime.of(2024, 7, 19, 8, 33, 6, 5, ZoneOffset.UTC)
    private val lotsOfTags =
        List(333) { i -> TagUiModel(dateTime = now.minusSeconds((i + 1) * (i + 1) - i + 5L)) }

    private val session = SessionDetailUiModel(
        name = "Test session name",
        nameIsBeingEdited = false,
        tags = List(6) { i -> TagUiModel(dateTime = now.minusSeconds(i * i + 2L)) },
        tagIdToScrollTo = null,
        deleteAllIsEnabled = true,
        tapAnywhereIsEnabled = false,
    )
    private val newSession = SessionDetailUiModel(
        name = null,
        nameIsBeingEdited = false,
        tags = listOf(),
        tagIdToScrollTo = null,
        deleteAllIsEnabled = false,
        tapAnywhereIsEnabled = false,
    )
    private val sessionWithScroll =
        session.copy(tags = lotsOfTags, tagIdToScrollTo = lotsOfTags.last().id)
    private val tapAnywhereSession = session.copy(tapAnywhereIsEnabled = true)
    private val tapAnywhereNewSession = newSession.copy(tapAnywhereIsEnabled = true)

    private val aTag = session.tags.let {
        val index = 2.coerceAtMost(it.size - 1)
        it[index]
    }

    private val successState = SessionDetailViewState.Success(session)
    private val newSuccessState = SessionDetailViewState.Success(newSession)
    private val tapAnywhereState = SessionDetailViewState.Success(tapAnywhereSession)
    private val tapAnywhereNewState = SessionDetailViewState.Success(tapAnywhereNewSession)
    private val sessionWithScrollState = SessionDetailViewState.Success(sessionWithScroll)
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
    fun successState_tappingEditCallsEventTapEdit() {
        setup(successState)
        tapEdit()
        verify(mockHandleEvent, times(1)).invoke(SessionDetailEvent.TapEdit)
    }

    @Test
    fun successState_typingNewNameDoesNotCallEventConfirmNameEdit() {
        setup(successState.editingName())
        val newName = "Banana"
        typeNewName(newName)
        verify(mockHandleEvent, never()).invoke(SessionDetailEvent.ConfirmNameEdit(newName))
    }

    @Test
    fun successState_tappingEditFinishCallsEventConfirmNameEdit() {
        setup(successState.editingName())
        val newName = "Croissant"
        typeNewName(newName)
        tapDoneEditingName()
        verify(mockHandleEvent, times(1)).invoke(SessionDetailEvent.ConfirmNameEdit(newName))
    }

    @Test
    fun successState_showsTags() {
        setup(successState)
        assertShowsTags(successState.session.tags)
    }

    @Test
    fun successState_deletingATagCallsEventTapConfirmDeleteTag() {
        setup(successState)
        deleteTag(aTag)
        verify(mockHandleEvent, times(1)).invoke(SessionDetailEvent.TapConfirmDeleteTag(aTag.id))
    }

    @Ignore("something weird going on when TapNewTagButton event is initialized, even in a verify")
    @Test
    fun successState_tappingAddButtonCallsEventTapNewTagButton() {
        setup(successState)
        assertAddTagButtonWorks()
    }

    @Test
    fun successState_tappingDeleteAllTagsButtonCallsTapDeleteAllTagsButton() {
        setup(successState)
        assertDeleteTagsButtonWorks()
    }

    @Test
    fun successState_tappingBackCallsEventTapBack() {
        setup(successState)
        composeTestRule
            .onNode(hasContentDescription(appContext.getString(R.string.back)))
            .performClick()
        verify(mockHandleEvent, times(1)).invoke(SessionDetailEvent.TapBack)
    }

    @Test
    fun successState_tappingSettingsCallsEventTapSettings() {
        setup(successState)
        composeTestRule
            .onNode(hasContentDescription(appContext.getString(R.string.settings)))
            .performClick()
        verify(mockHandleEvent, times(1)).invoke(SessionDetailEvent.TapSettings)
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
    fun newSuccessState_showsUntitled() {
        setup(newSuccessState)
        composeTestRule.onNodeWithText(appContext.getString(R.string.untitled))
            .assertIsDisplayed()
    }

    @Test
    fun newSuccessState_tappingEditCallsEventTapEdit() {
        setup(newSuccessState)
        tapEdit()
        verify(mockHandleEvent, times(1)).invoke(SessionDetailEvent.TapEdit)
    }

    @Test
    fun newSuccessState_typingNewNameDoesNotCallEventConfirmNameEdit() {
        setup(newSuccessState.editingName())
        val newName = "Donut"
        typeNewName(newName)
        verify(mockHandleEvent, never()).invoke(SessionDetailEvent.ConfirmNameEdit(newName))
    }

    @Test
    fun newSuccessState_tappingEditFinishCallsEventConfirmNameEdit() {
        setup(newSuccessState.editingName())
        val newName = "Eclair"
        typeNewName(newName)
        tapDoneEditingName()
        verify(mockHandleEvent, times(1)).invoke(SessionDetailEvent.ConfirmNameEdit(newName))
    }

    @Test
    fun newSuccessState_showsNoTagMessage() {
        setup(newSuccessState)
        composeTestRule.onNodeWithText(appContext.getString(R.string.no_tags_tap_below))
            .assertIsDisplayed()
    }

    @Ignore("something weird going on when TapNewTagButton event is initialized, even in a verify")
    @Test
    fun newSuccessState_tappingAddButtonCallsEventTapNewTagButton() {
        setup(newSuccessState)
        assertAddTagButtonWorks()
    }

    @Test
    fun newSuccessState_hasDisabledDeleteTagsButton() {
        setup(newSuccessState)
        assertDeleteTagsButtonDisabled()
    }

    @Test
    fun newSuccessState_tappingBackCallsEventTapBack() {
        setup(newSuccessState)
        composeTestRule
            .onNode(hasContentDescription(appContext.getString(R.string.back)))
            .performClick()
        verify(mockHandleEvent, times(1)).invoke(SessionDetailEvent.TapBack)
    }

    @Test
    fun newSuccessState_tappingSettingsCallsEventTapSettings() {
        setup(newSuccessState)
        composeTestRule
            .onNode(hasContentDescription(appContext.getString(R.string.settings)))
            .performClick()
        verify(mockHandleEvent, times(1)).invoke(SessionDetailEvent.TapSettings)
    }

    // tap anywhere state

    @Ignore("something weird going on when TapAnywhere event is initialized, even in a verify")
    @Test
    fun tapAnywhereState_tappingEditDoesNotCallEventTapAnywhere() {
        setup(tapAnywhereState)
        tapEdit()
        verify(mockHandleEvent, times(1)).invoke(SessionDetailEvent.TapEdit)
        verifyTapAnywhereIsNotCalled()
    }

    @Ignore("something weird going on when TapAnywhere event is initialized, even in a verify")
    @Test
    fun tapAnywhereState_deletingATagDoesNotCallEventTapAnywhere() {
        setup(tapAnywhereState)
        deleteTag(aTag)
        verify(mockHandleEvent, times(1)).invoke(SessionDetailEvent.TapConfirmDeleteTag(aTag.id))
        verifyTapAnywhereIsNotCalled()
    }

    @Ignore("something weird going on when TapAnywhere event is initialized, even in a verify")
    @Test
    fun tapAnywhereState_tappingAddButtonDoesNotCallEventTapAnywhere() {
        setup(tapAnywhereState)
        assertAddTagButtonWorks()
        verifyTapAnywhereIsNotCalled()
    }

    @Ignore("something weird going on when TapAnywhere event is initialized, even in a verify")
    @Test
    fun tapAnywhereState_tappingDeleteAllTagsButtonDoesNotCallEventTapAnywhere() {
        setup(tapAnywhereState)
        assertDeleteTagsButtonWorks()
        verifyTapAnywhereIsNotCalled()
    }

    @Ignore("something weird going on when TapAnywhere event is initialized, even in a verify")
    @Test
    fun tapAnywhereState_tappingBackDoesNotCallEventTapAnywhere() {
        setup(tapAnywhereState)
        composeTestRule
            .onNode(hasContentDescription(appContext.getString(R.string.back)))
            .performClick()
        verify(mockHandleEvent, times(1)).invoke(SessionDetailEvent.TapBack)
        verifyTapAnywhereIsNotCalled()
    }

    @Ignore("something weird going on when TapAnywhere event is initialized, even in a verify")
    @Test
    fun tapAnywhereState_tappingSettingsDoesNotCallEventTapAnywhere() {
        setup(successState)
        composeTestRule
            .onNode(hasContentDescription(appContext.getString(R.string.settings)))
            .performClick()
        verify(mockHandleEvent, times(1)).invoke(SessionDetailEvent.TapSettings)
        verifyTapAnywhereIsNotCalled()
    }

    @Ignore("something weird going on when TapAnywhere event is initialized, even in a verify")
    @Test
    fun tapAnywhereState_tappingExistingTagCallsEventTapAnywhere() {
        setup(tapAnywhereState)
        composeTestRule.onNodeWithText(aTag.dateTime.formatShortDateLongTime()).performClick()
        verify(mockHandleEvent, times(1)).invoke(SessionDetailEvent.TapAnywhere(any()))
    }

    @Ignore("Unsure how to click _below_ a node")
    @Test
    fun tapAnywhereState_tappingOnBackgroundCallsEventTapAnywhere() {
        setup(tapAnywhereState)
        val lastTag = tapAnywhereSession.tags.last()
        composeTestRule.onNodeWithText(lastTag.dateTime.formatShortDateLongTime()).performClick()
        verify(mockHandleEvent, times(1)).invoke(SessionDetailEvent.TapAnywhere(any()))
    }

    @Ignore("something weird going on when TapAnywhere event is initialized, even in a verify")
    @Test
    fun tapAnywhereNewState_tappingOnTextCallsEventTapAnywhere() {
        setup(tapAnywhereNewState)
        composeTestRule.onNodeWithText(appContext.getString(R.string.no_tags_tap_anywhere))
            .performClick()
        verify(mockHandleEvent, times(1)).invoke(SessionDetailEvent.TapAnywhere(any()))
    }

    // scroll to tag

    @Test
    fun sessionWithScrollState_scrollsToTag() {
        setup(sessionWithScrollState)
        val firstTag = sessionWithScroll.tags.first()
        val tagToScrollTo =
            sessionWithScroll.tags.find { it.id == sessionWithScroll.tagIdToScrollTo }!!

        composeTestRule
            .onNodeWithText(tagToScrollTo.dateTime.formatShortDateLongTime())
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText(firstTag.dateTime.formatShortDateLongTime())
            .assertIsNotDisplayed()
    }

    // loading

    @Test
    fun successState_hasProgressIndicator() {
        setup(loadingState)
        getProgressIndicator().assertIsDisplayed()
    }

    @Test
    fun loadingState_hasTitleText() {
        setup(loadingState)
        composeTestRule
            .onNodeWithText(appContext.getString(R.string.session_detail))
            .assertIsDisplayed()
    }

    @Test
    fun loadingState_tappingBackCallsEventTapBack() {
        setup(loadingState)
        composeTestRule
            .onNode(hasContentDescription(appContext.getString(R.string.back)))
            .performClick()
        verify(mockHandleEvent, times(1)).invoke(SessionDetailEvent.TapBack)
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

    @Test
    fun errorState_hasTitleText() {
        setup(errorState)
        composeTestRule
            .onNodeWithText(appContext.getString(R.string.session_detail))
            .assertIsDisplayed()
    }

    @Test
    fun errorState_tappingBackCallsEventTapBack() {
        setup(errorState)
        composeTestRule
            .onNode(hasContentDescription(appContext.getString(R.string.back)))
            .performClick()
        verify(mockHandleEvent, times(1)).invoke(SessionDetailEvent.TapBack)
    }

    @Test
    fun errorState_tappingSettingsCallsEventTapSettings() {
        setup(errorState)
        composeTestRule
            .onNode(hasContentDescription(appContext.getString(R.string.settings)))
            .performClick()
        verify(mockHandleEvent, times(1)).invoke(SessionDetailEvent.TapSettings)
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

    @Ignore("Refresh state never produced by view model, and action would trigger event")
    @Test
    fun refreshState_tappingEditDoesNotCallEventTapEdit() {
        setup(refreshState)
        tapEdit()
        verify(mockHandleEvent, never()).invoke(SessionDetailEvent.TapEdit)
    }

    @Test
    fun refreshState_showsTags() {
        setup(refreshState)
        assertShowsTags(refreshState.session.tags)
    }

    @Ignore("Refresh state never produced by view model, and button is enabled")
    @Test
    fun refreshState_hasDisabledAddTagButton() {
        setup(refreshState)
        assertAddTagButtonDisabled()
    }

    @Ignore("Refresh state never produced by view model, and button may be enabled")
    @Test
    fun refreshState_hasDisabledDeleteTagsButton() {
        setup(refreshState)
        assertDeleteTagsButtonDisabled()
    }

    // helpers

    private fun getProgressIndicator() =
        composeTestRule.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo))

    private fun getErrorMessage() =
        composeTestRule.onNodeWithText(appContext.getString(R.string.error_session_detail))

    private fun tapEdit() {
        composeTestRule.onNode(hasContentDescription(appContext.getString(R.string.edit)))
            .performClick()
    }

    private fun typeNewName(newName: String) {
        composeTestRule.onNodeWithContentDescription(appContext.getString(R.string.name_input))
            .performTextReplacement(newName)
    }

    private fun tapDoneEditingName() {
        composeTestRule.onNodeWithContentDescription(appContext.getString(R.string.name_input))
            .performImeAction()
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

    private fun assertAddTagButtonDisabled() {
        composeTestRule.onNodeWithText(appContext.getString(R.string.add_tag)).apply {
            assertHasClickAction()
            assertIsNotEnabled()
            performClick()
        }
        verify(mockHandleEvent, never()).invoke(SessionDetailEvent.TapNewTagButton(any()))
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

    private fun verifyTapAnywhereIsNotCalled() {
        verify(mockHandleEvent, never()).invoke(SessionDetailEvent.TapAnywhere(any()))
    }
}

private fun SessionDetailViewState.Success.editingName() =
    copy(session = session.copy(nameIsBeingEdited = true))
