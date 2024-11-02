package com.example.space_timetagger.sessions.data.repository.test

import com.example.space_timetagger.CoroutineTestRule
import com.example.space_timetagger.sessions.data.repository.SessionsRepositoryImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class SessionsRepositoryImplTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    var coroutineTestRule = CoroutineTestRule()

    @Mock
    private val mockSessionsRepository = SessionsRepositoryImpl()

    @Test
    fun dummyTest() = runTest {
        assert(true)
    }
}