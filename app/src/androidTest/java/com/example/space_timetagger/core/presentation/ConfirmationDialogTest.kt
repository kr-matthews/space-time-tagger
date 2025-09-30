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

    private val onConfirm: () -> Unit = mock()
    private val onCancel: () -> Unit = mock()

    @Before
    fun setup() {
        composeTestRule.setContent {
            SpaceTimeTaggerTheme {
                ConfirmationDialog(onConfirm, onCancel)
            }
        }
    }

    @Test
    fun confirmButton_callsActionAndDismissRequest() {
        composeTestRule.onNodeWithText(appContext.getString(R.string.confirm)).performClick()
        verify(onConfirm, times(1)).invoke()
        verify(onCancel, times(0)).invoke()
    }

    @Test
    fun cancelButton_callsOnlyDismissRequest() {
        composeTestRule.onNodeWithText(appContext.getString(R.string.cancel)).performClick()
        verify(onConfirm, times(0)).invoke()
        verify(onCancel, times(1)).invoke()
    }

    // TODO: test onDismiss functionality - not sure how to click outside of the dialog
}
