package com.example.space_timetagger.ui.session.test

import com.example.space_timetagger.domain.model.TagModel
import com.example.space_timetagger.domain.repository.SessionsRepository
import com.example.space_timetagger.ui.CoroutineTestRule
import com.example.space_timetagger.ui.mockDateTime
import com.example.space_timetagger.ui.mockSession
import com.example.space_timetagger.ui.mockTag
import com.example.space_timetagger.ui.session.SessionViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

const val sessionId = "fake-id"

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class SessionViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    var coroutineTestRule = CoroutineTestRule()

    @Mock
    private val mockSessionsRepository = mock<SessionsRepository>()

    private lateinit var viewModel: SessionViewModel

    @Before
    fun setup() = runTest {
        whenever(mockSessionsRepository.session(sessionId)).thenReturn(flowOf(mockSession))
        viewModel = SessionViewModel(sessionId, mockSessionsRepository)
    }

    @Test
    fun initialState_hasNameFromRepository() = runTest {
        val initialSession = viewModel.session.first()
        assertEquals(mockSession.name, initialSession?.name)
    }

    @Test
    fun initialState_hasTagsFromRepository() = runTest {
        val initialSession = viewModel.session.first()
        assertEquals(mockSession.tags, initialSession?.tags)
    }

    @Test
    fun setNameCallback_callsRepositoryFunc() = runTest {
        val newName = "Updated Name String"
        viewModel.callbacks.setName(newName)
        advanceUntilIdle()
        verify(mockSessionsRepository, times(1)).renameSession(sessionId, newName)
    }

    @Test
    fun addTagCallback_callsRepositoryFunc() = runTest {
        viewModel.callbacks.addTag(mockDateTime)
        advanceUntilIdle()
        verify(mockSessionsRepository, times(1)).addTagToSession(sessionId, TagModel(mockDateTime))
    }

    @Test
    fun deleteTagCallback_callsRepositoryFunc() = runTest {
        viewModel.callbacks.deleteTag(mockTag)
        advanceUntilIdle()
        verify(mockSessionsRepository, times(1)).removeTagFromSession(sessionId, mockTag)
    }

    @Test
    fun clearTagsCallback_callsRepositoryFunc() = runTest {
        viewModel.callbacks.clearTags()
        advanceUntilIdle()
        verify(mockSessionsRepository, times(1)).removeAllTagsFromSession(sessionId)
    }
}
