package com.example.space_timetagger.sessions.data.repository.test

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.example.space_timetagger.CoroutineTestRule
import com.example.space_timetagger.database.data.RoomSessionsDataSource
import com.example.space_timetagger.sessions.data.repository.SessionsRepositoryImpl
import com.example.space_timetagger.sessions.domain.mockSession
import com.example.space_timetagger.sessions.domain.mockSessions
import com.example.space_timetagger.sessions.domain.mockTag
import com.example.space_timetagger.sessions.domain.repository.SessionsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.argThat
import org.mockito.kotlin.eq
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class SessionsRepositoryImplTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    var coroutineTestRule = CoroutineTestRule()

    @Mock
    private val sessionsDataSource = mock<RoomSessionsDataSource>()

    private lateinit var mockSessionsRepository: SessionsRepository

    @Before
    fun setup() = runTest {
        whenever(sessionsDataSource.getSessions()).thenReturn(flowOf(mockSessions))
        whenever(sessionsDataSource.getSession(mockSession.id)).thenReturn(flowOf(mockSession))
        mockSessionsRepository = SessionsRepositoryImpl(sessionsDataSource)
    }

    @Test
    fun sessions_producesSessionsFromDataSource() = runTest {
        val sessions = mockSessionsRepository.sessions().first()
        assertThat(sessions).isEqualTo(mockSessions)
    }

    @Test
    fun session_producesSessionFromDataSource() = runTest {
        val session = mockSessionsRepository.session(mockSession.id).first()
        assertThat(session).isEqualTo(mockSession)
    }

    @Test
    fun newSession_withName_callsDataSourceUpsert() = runTest {
        val name = "Session Test Name"
        mockSessionsRepository.newSession(name)
        verify(sessionsDataSource, times(1)).upsertSessionWithoutTags(
            argThat { session -> session.name == name && session.tags.isEmpty() },
        )
    }

    @Test
    fun newSession_withoutName_callsDataSourceUpsert() = runTest {
        mockSessionsRepository.newSession()
        verify(sessionsDataSource, times(1)).upsertSessionWithoutTags(
            argThat { session -> session.name == null && session.tags.isEmpty() },
        )
    }

    @Test
    fun renameSession_withName_callsDataSourceUpsert() = runTest {
        val newName = "Session Test (new) Name"
        mockSessionsRepository.renameSession(mockSession.id, newName)
        verify(sessionsDataSource, times(1)).upsertSessionWithoutTags(
            argThat { session -> session.id == mockSession.id && session.name == newName },
        )
    }

    @Test
    fun renameSession_withoutName_callsDataSourceUpsert() = runTest {
        mockSessionsRepository.renameSession(mockSession.id, null)
        verify(sessionsDataSource, times(1)).upsertSessionWithoutTags(
            argThat { session -> session.id == mockSession.id && session.name == null },
        )
    }

    @Test
    fun addTagToSession_callsDataSourceUpsert() = runTest {
        mockSessionsRepository.addTagToSession(mockSession.id, mockTag)
        verify(sessionsDataSource, times(1)).upsertTag(
            mockSession.id,
            mockTag,
        )
    }

    @Test
    fun toggleTagArchived_callsDataSourceUpsert() = runTest {
        mockSessionsRepository.toggleTagArchived(mockSession.id, mockTag)
        verify(sessionsDataSource, times(1)).upsertTag(
            eq(mockSession.id),
            argThat { tag -> tag.id == mockTag.id && tag.isArchived != mockTag.isArchived },
        )
    }

    @Test
    fun removeTag_callsDataSourceDelete() = runTest {
        mockSessionsRepository.removeTag(mockSession.id, mockTag.id)
        verify(sessionsDataSource, times(1)).deleteTag(mockTag.id)
    }

    @Test
    fun removeAllTagsFromSession_callsDataSourceDelete() = runTest {
        mockSessionsRepository.removeAllTagsFromSession(mockSession.id)
        verify(sessionsDataSource, times(1)).clearTags(mockSession.id)
    }

    @Test
    fun deleteSession_callsDataSourceDelete() = runTest {
        mockSessionsRepository.deleteSession(mockSession.id)
        verify(sessionsDataSource, times(1)).deleteSession(mockSession.id)
    }

    @Test
    fun deleteAllSessions_callsDataSourceDelete() = runTest {
        mockSessionsRepository.deleteAllSessions()
        verify(sessionsDataSource, times(1)).clearSessions()
    }
}