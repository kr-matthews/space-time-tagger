package com.example.space_timetagger.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesOf
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.example.space_timetagger.CoroutineTestRule
import com.example.space_timetagger.core.domain.repository.PreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class PreferencesRepositoryImplTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    var coroutineTestRule = CoroutineTestRule()

    @Mock
    private lateinit var preferencesDataStore: DataStore<Preferences>

    private lateinit var preferencesRepository: PreferencesRepository

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun initializeRepository(
        mockPreferences: Preferences?,
        initiallyHasFineLocationPermission: Boolean = true,
    ) {
        mockPreferences?.let { whenever(preferencesDataStore.data).thenReturn(flowOf(it)) }
        preferencesRepository = PreferencesRepositoryImpl(
            preferencesDataStore = preferencesDataStore,
            initiallyHasFineLocationPermission = initiallyHasFineLocationPermission,
            ioDispatcher = coroutineTestRule.testDispatcher,
        )
    }

    @Test
    fun initiallyWithEmptyPreferences_taggingLocationIsEnabledProducesFalse() = runTest {
        initializeRepository(mockEmptyPreferences)
        assertThat(preferencesRepository.taggingLocationIsEnabled.first()).isFalse()
    }

    @Test
    fun initiallyWithEnabledLocationPreferencesAndPermission_taggingLocationIsEnabledProducesTrue() =
        runTest {
            initializeRepository(mockEnabledLocationPreferences, true)
        assertThat(preferencesRepository.taggingLocationIsEnabled.first()).isTrue()
            verify(preferencesDataStore, never()).edit(any())
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun initiallyWithEnabledLocationPreferencesButNoPermission_callsDataStoreEdit() = runTest {
        initializeRepository(mockEnabledLocationPreferences, false)
        advanceUntilIdle()
        verify(preferencesDataStore).edit(any())
    }

    @Test
    fun initiallyWithDisabledLocationPreferences_taggingLocationIsEnabledProducesFalse() = runTest {
        initializeRepository(mockDisabledLocationPreferences)
        assertThat(preferencesRepository.taggingLocationIsEnabled.first()).isFalse()
    }

    @Ignore("Says checked exception is invalid for this method, not sure why")
    @Test
    fun whenDataStoreThrowsIOException_taggingLocationIsEnabledProducesFalse() = runTest {
        whenever(preferencesDataStore.data).thenThrow(IOException())
        initializeRepository(null)
        assertThat(preferencesRepository.taggingLocationIsEnabled.first()).isFalse()
    }

    // unsure how best to write test
    @Test
    fun enableTaggingLocation_callsDataStoreEdit() = runTest {
        initializeRepository(mockDisabledLocationPreferences)
        preferencesRepository.enableTaggingLocation()
        verify(preferencesDataStore).edit(any())
    }

    // unsure how best to write test
    @Test
    fun disableTaggingLocation_callsDataStoreEdit() = runTest {
        initializeRepository(mockEnabledLocationPreferences)
        preferencesRepository.disableTaggingLocation()
        verify(preferencesDataStore).edit(any())
    }

    private val mockEmptyPreferences = emptyPreferences()
    private val mockEnabledLocationPreferences = preferencesOf(TAGGING_LOCATION to true)
    private val mockDisabledLocationPreferences = preferencesOf(TAGGING_LOCATION to false)
}