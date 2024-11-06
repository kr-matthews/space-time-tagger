package com.example.space_timetagger.core.presentation

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

fun OffsetDateTime.formatShortDateLongTime(): String =
    format(DateTimeFormatter.ofPattern("MMM dd, HH:mm.ss"))
