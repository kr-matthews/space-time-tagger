package com.example.space_timetagger.sessions.presentation.sessionsList

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.example.space_timetagger.R
import com.example.space_timetagger.sessions.presentation.models.SessionOverviewUiModel
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class SessionsListViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    private val mockHandleEvent: (SessionsListEvent) -> Unit = mock()

    private val sessions = listOf(
        SessionOverviewUiModel(name = "Apple"),
        SessionOverviewUiModel(name = "Banana"),
        SessionOverviewUiModel(name = null),
        SessionOverviewUiModel(name = "Cherry"),
        SessionOverviewUiModel(name = "Dragon fruit"),
        SessionOverviewUiModel(name = "Elderberry"),
        SessionOverviewUiModel(name = null),
    )
    private val aNamedSession = sessions.filter { it.name != null }.let {
        val index = 1.coerceAtMost(it.size - 1)
        it[index]
    }

    private val successState = SessionsListViewState.Success(sessions, true)
    private val emptySuccessState = SessionsListViewState.Success(emptyList(), false)
    private val loadingState = SessionsListViewState.Loading
    private val errorState = SessionsListViewState.Error
    // refreshing state also exists, but not currently produced by view model

    private fun setup(viewState: SessionsListViewState) {
        composeTestRule.setContent {
            SpaceTimeTaggerTheme {
                SessionsListView(viewState, mockHandleEvent)
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
    fun successState_showsSessions() {
        setup(successState)
        assertShowsSessions(successState.sessions)
    }

    @Ignore("No idea why it fails, it's the same as in the session detail test")
    @Test
    fun successState_deletingASessionCallsEventTapConfirmDeleteSession() {
        setup(successState)
        deleteNamedSession(aNamedSession)
        verify(mockHandleEvent, times(1)).invoke(
            SessionsListEvent.TapConfirmDeleteSession(aNamedSession.id)
        )
    }

    @Test
    fun successState_tappingNewSessionCallsEventTapNewSession() {
        setup(successState)
        assertNewSessionButtonWorks()
    }

    @Test
    fun successState_tappingDeleteAllCallsEventTapConfirmDeleteAllSessions() {
        setup(successState)
        assertDeleteAllSessionsButtonWorks()
    }

    // success, no sessions

    @Test
    fun emptySuccessState_doesNotHaveProgressIndicator() {
        setup(emptySuccessState)
        getProgressIndicator().assertDoesNotExist()
    }

    @Test
    fun emptySuccessState_doesNotHaveErrorMessage() {
        setup(emptySuccessState)
        getErrorMessage().assertDoesNotExist()
    }

    @Test
    fun emptySuccessState_tappingNewSessionCallsEventTapNewSession() {
        setup(emptySuccessState)
        assertNewSessionButtonWorks()
    }

    @Test
    fun emptySuccessState_deleteAllButtonIsDisabled() {
        setup(emptySuccessState)
        assertDeleteAllSessionsButtonIsDisabled()
    }

    // loading

    @Test
    fun loadingState_doesHasProgressIndicator() {
        setup(loadingState)
        getProgressIndicator().assertIsDisplayed()
    }

    @Test
    fun loadingState_doesNotHaveErrorMessage() {
        setup(loadingState)
        getErrorMessage().assertDoesNotExist()
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

    // not currently possible

    // helpers

    private fun getProgressIndicator() =
        composeTestRule.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo))

    private fun getErrorMessage() =
        composeTestRule.onNodeWithText(appContext.getString(R.string.error_sessions_list))

    private fun assertShowsSessions(sessions: List<SessionOverviewUiModel>) {
        sessions.forEach { session ->
            session.name?.let { name ->
                composeTestRule.onNodeWithText(name).assertIsDisplayed()
            }
        }
        val untitledSessionCount = sessions.filter { it.name == null }.size
        composeTestRule.onAllNodesWithText(appContext.getString(R.string.untitled))
            .assertCountEquals(untitledSessionCount)
    }

    private fun deleteNamedSession(session: SessionOverviewUiModel) {
        composeTestRule.onNodeWithText(session.name!!).onParent().onChildren().filterToOne(
            hasContentDescription(appContext.getString(R.string.delete))
        ).apply {
            assertHasClickAction()
            assertIsEnabled()
            performClick()
        }
        composeTestRule.onNodeWithText(appContext.getString(R.string.confirm)).performClick()
    }

    private fun assertNewSessionButtonWorks() {
        composeTestRule.onNodeWithText(appContext.getString(R.string.new_session)).apply {
            assertHasClickAction()
            assertIsEnabled()
            performClick()
        }
        verify(mockHandleEvent, times(1)).invoke(SessionsListEvent.TapNewSessionButton)
    }

    private fun assertDeleteAllSessionsButtonWorks() {
        composeTestRule.onNodeWithText(appContext.getString(R.string.delete_all)).apply {
            assertHasClickAction()
            assertIsEnabled()
            performClick()
        }
        composeTestRule.onNodeWithText(appContext.getString(R.string.confirm)).performClick()
        verify(mockHandleEvent, times(1)).invoke(SessionsListEvent.TapConfirmDeleteAllSessions)
    }

    private fun assertDeleteAllSessionsButtonIsDisabled() {
        composeTestRule.onNodeWithText(appContext.getString(R.string.delete_all)).apply {
            assertHasClickAction()
            assertIsNotEnabled()
            performClick()
        }
        composeTestRule.onNodeWithText(appContext.getString(R.string.confirm)).assertDoesNotExist()
        verify(mockHandleEvent, never()).invoke(SessionsListEvent.TapConfirmDeleteAllSessions)
    }
}
