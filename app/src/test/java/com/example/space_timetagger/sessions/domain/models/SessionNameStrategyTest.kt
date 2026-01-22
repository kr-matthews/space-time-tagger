package com.example.space_timetagger.sessions.domain.models

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDateTime

@RunWith(MockitoJUnitRunner::class)
class SessionNameStrategyTest {
    val mockNow: LocalDateTime = LocalDateTime.of(2026, 1, 8, 7, 2)

    @Test
    fun invokingAsk_returnsNull() = runTest {
        assertThat(SessionNameStrategy.ASK.invoke(mockNow)).isNull()
    }

    @Test
    fun invokingEmpty_returnsEmpty() = runTest {
        assertThat(SessionNameStrategy.EMPTY.invoke(mockNow)).isEqualTo("")
    }

    @Test
    fun invokingDate_returnsDate() = runTest {
        assertThat(SessionNameStrategy.DATE.invoke(mockNow)).isEqualTo("2026-01-08")
    }

    @Test
    fun invokingDayOfWeek_returnsDayOfWeek() = runTest {
        assertThat(SessionNameStrategy.DAY_OF_WEEK.invoke(mockNow)).isEqualTo("Thursday")
    }

    @Test
    fun invokingDayOfWeekAndDay_returnsDayOfWeekAndDay() = runTest {
        assertThat(SessionNameStrategy.DAY_OF_WEEK_AND_DAY.invoke(mockNow)).isEqualTo("Thu, Jan 08")
    }

    @Test
    fun invokingDayOfWeekAndTime_returnsDayOfWeekAndTime() = runTest {
        assertThat(SessionNameStrategy.DAY_OF_WEEK_AND_TIME.invoke(mockNow)).isEqualTo("Thu, 07:02")
    }

    @Test
    fun toSessionNameStrategy_parseExpectedStrings() = runTest {
        assertThat(
            SessionNameStrategy.DAY_OF_WEEK.name.toSessionNameStrategy()
        ).isEqualTo(SessionNameStrategy.DAY_OF_WEEK)
        assertThat(
            SessionNameStrategy.ASK.name.toSessionNameStrategy()
        ).isEqualTo(SessionNameStrategy.ASK)
        assertThat(
            defaultSessionNameStrategy.name.toSessionNameStrategy()
        ).isEqualTo(defaultSessionNameStrategy)
    }

    @Test
    fun toSessionNameStrategy_defaultsOnUnexpectedString() = runTest {
        assertThat("Random string".toSessionNameStrategy()).isEqualTo(defaultSessionNameStrategy)
    }
}
