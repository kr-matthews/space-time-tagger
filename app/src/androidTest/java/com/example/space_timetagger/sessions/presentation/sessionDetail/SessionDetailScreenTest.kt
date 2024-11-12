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
import com.example.space_timetagger.sessions.domain.models.SessionCallbacks
import com.example.space_timetagger.sessions.presentation.models.SessionDetailUi
import com.example.space_timetagger.sessions.presentation.models.TagUi
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import java.time.OffsetDateTime
import java.time.ZoneOffset

class SessionDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    private val mockCallbacks: SessionCallbacks = mock()
    private val now: OffsetDateTime = OffsetDateTime.of(2024, 7, 19, 8, 33, 6, 5, ZoneOffset.UTC)
    private val session = SessionDetailUi(
        name = "Test session name",
        tags = List(6) { i -> TagUi(dateTime = now.minusSeconds(i * i + 2L)) },
    )
    private val successState = SessionDetailUiState.Success(session)
    private val newSuccessState = SessionDetailUiState.Success(
        SessionDetailUi(name = null, tags = listOf()),
    )
    private val loadingState = SessionDetailUiState.Loading
    private val errorState = SessionDetailUiState.Error
    private val refreshState = SessionDetailUiState.Refreshing(session)

    private fun setup(viewState: SessionDetailUiState) {
        composeTestRule.setContent {
            SpaceTimeTaggerTheme {
                SessionDetailView(viewState, mockCallbacks)
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
        verify(mockCallbacks, times(1)).setName(newName)
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
        verify(mockCallbacks, times(1)).deleteTag(tagToDelete.id)
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
        verify(mockCallbacks, times(1)).setName(newName)
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

    private fun assertShowsTags(tags: List<TagUi>) {
        tags.forEach {
            composeTestRule.onNodeWithText(it.dateTime.formatShortDateLongTime())
                .assertIsDisplayed()
        }
    }

    private fun deleteTag(tag: TagUi) {
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
        verify(mockCallbacks, times(1)).addTag(any())
    }

    private fun assertDeleteTagsButtonWorks() {
        composeTestRule.onNodeWithText(appContext.getString(R.string.delete_all)).apply {
            assertIsEnabled()
            performClick()
        }
        composeTestRule.onNodeWithText(appContext.getString(R.string.confirm)).performClick()
        verify(mockCallbacks, times(1)).deleteAllTags()
    }

    private fun assertDeleteTagsButtonDisabled() {
        composeTestRule.onNodeWithText(appContext.getString(R.string.delete_all)).apply {
            assertHasClickAction()
            assertIsNotEnabled()
            performClick()
        }
        composeTestRule.onNodeWithText(appContext.getString(R.string.confirm)).assertDoesNotExist()
        verify(mockCallbacks, times(0)).deleteAllTags()
    }
}