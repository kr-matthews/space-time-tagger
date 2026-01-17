package com.example.space_timetagger.sessions.domain.models

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class SessionNameStrategy(private val display: String, val dateTimePattern: String?) {
    ASK("Ask", null),
    EMPTY("Empty", ""),
    DATE("Date", "yyyy-MM-dd"),
    DAY_OF_WEEK("Day of Week", "EEEE"),
    DAY_OF_WEEK_AND_DAY("Day of Week and Day", "EE, MMM dd"),
    DAY_OF_WEEK_AND_TIME("Day of Week and Time", "EE, HH:mm");

    override fun toString() = display

    fun invoke(now: LocalDateTime = LocalDateTime.now()) = dateTimePattern?.let {
        now.format(DateTimeFormatter.ofPattern(dateTimePattern))
    }

    fun displayWithExample() =
        "$display${invoke().let { eg -> if (eg.isNullOrEmpty()) "" else ", e.g. $eg" }}"
}

val defaultSessionNameStrategy = SessionNameStrategy.DAY_OF_WEEK_AND_DAY

fun String.toSessionNameStrategy(): SessionNameStrategy {
    SessionNameStrategy.entries.forEach { if (it.name == this) return it }
    return defaultSessionNameStrategy
}
