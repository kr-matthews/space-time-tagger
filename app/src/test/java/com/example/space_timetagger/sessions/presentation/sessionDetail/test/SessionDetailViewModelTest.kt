package com.example.space_timetagger.sessions.presentation.sessionDetail.test

import com.example.space_timetagger.CoroutineTestRule
import com.example.space_timetagger.sessions.domain.mockDateTime
import com.example.space_timetagger.sessions.domain.mockSession
import com.example.space_timetagger.sessions.domain.mockTag
import com.example.space_timetagger.sessions.domain.repository.SessionsRepository
import com.example.space_timetagger.sessions.presentation.sessionDetail.SessionDetailState
import com.example.space_timetagger.sessions.presentation.sessionDetail.SessionViewModel
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
import org.mockito.kotlin.argThat
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class SessionDetailViewModelTest {
    private val validId = "existing-id"
    private val nonExistentId = "not-found-id"

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    var coroutineTestRule = CoroutineTestRule()

    @Mock
    private val mockSessionsRepository = mock<SessionsRepository>()

    private lateinit var viewModel: SessionViewModel
    private lateinit var viewModelNonExistentSession: SessionViewModel

    @Before
    fun setup() = runTest {
        whenever(mockSessionsRepository.session(validId)).thenReturn(flowOf(mockSession))
        whenever(mockSessionsRepository.session(nonExistentId)).thenReturn(flowOf(null))
        viewModel = SessionViewModel(validId, mockSessionsRepository)
        viewModelNonExistentSession = SessionViewModel(nonExistentId, mockSessionsRepository)
    }

    @Test
    fun nonExistentIdInitialState_producesErrorState() = runTest {
        val initialViewState = viewModelNonExistentSession.viewState.first()
        assert(initialViewState is SessionDetailState.Error)
    }

    @Test
    fun initialState_isSuccessState() = runTest {
        val initialViewState = viewModel.viewState.first()
        assert(initialViewState is SessionDetailState.Success)
    }

    @Test
    fun initialSuccessState_hasNameFromRepository() = runTest {
        val initialViewState = viewModel.viewState.first() as SessionDetailState.Success
        assertEquals(mockSession.name, initialViewState.session.name)
    }

    @Test
    fun initialSuccessState_hasTagsFromRepository() = runTest {
        val initialViewState = viewModel.viewState.first() as SessionDetailState.Success
        assertEquals(mockSession.tags.map { it.id }, initialViewState.session.tags.map { it.id })
    }

    @Test
    fun setNameCallback_callsRepositoryFunc() = runTest {
        val newName = "Updated Name String"
        viewModel.callbacks.setName(newName)
        advanceUntilIdle()
        verify(mockSessionsRepository, times(1)).renameSession(validId, newName)
    }

    @Test
    fun addTagCallback_callsRepositoryFunc() = runTest {
        viewModel.callbacks.addTag(mockDateTime)
        advanceUntilIdle()
        verify(mockSessionsRepository, times(1)).addTagToSession(
            eq(validId),
            argThat { tag -> tag.dateTime == mockDateTime },
        )
    }

    @Test
    fun deleteTagCallback_callsRepositoryFunc() = runTest {
        viewModel.callbacks.deleteTag(mockTag.id)
        advanceUntilIdle()
        verify(mockSessionsRepository, times(1)).removeTagFromSession(validId, mockTag.id)
    }

    @Test
    fun deleteAllTagsCallback_callsRepositoryFunc() = runTest {
        viewModel.callbacks.deleteAllTags()
        advanceUntilIdle()
        verify(mockSessionsRepository, times(1)).removeAllTagsFromSession(validId)
    }
}
