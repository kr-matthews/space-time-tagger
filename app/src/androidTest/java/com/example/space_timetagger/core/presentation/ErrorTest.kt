package com.example.space_timetagger.core.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.space_timetagger.ui.theme.SpaceTimeTaggerTheme
import org.junit.Rule
import org.junit.Test


class ErrorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysString() {
        val errorString = "error message"

        composeTestRule.setContent {
            SpaceTimeTaggerTheme {
                Error(errorString)
            }
        }

        composeTestRule.onNodeWithText(errorString).assertIsDisplayed()
    }
}