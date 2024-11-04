package com.example.space_timetagger.core.presentation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.example.space_timetagger.R
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class ConfirmationDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    private val onDismissRequest: () -> Unit = mock()
    private val action: () -> Unit = mock()

    @Before
    fun setup() {
        composeTestRule.setContent {
            SpaceTimeTaggerTheme {
                ConfirmationDialog(onDismissRequest, action)
            }
        }
    }

    @Test
    fun confirmButton_callsActionAndDismissRequest() {
        composeTestRule.onNodeWithText(appContext.getString(R.string.confirm)).performClick()
        verify(action, times(1)).invoke()
        verify(onDismissRequest, times(1)).invoke()
    }

    @Test
    fun cancelButton_callsOnlyDismissRequest() {
        composeTestRule.onNodeWithText(appContext.getString(R.string.cancel)).performClick()
        verify(action, times(0)).invoke()
        verify(onDismissRequest, times(1)).invoke()
    }
}
