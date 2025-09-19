package com.example.space_timetagger.core.presentation

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDateTime.formatShortDateLongTime(): String =
    format(DateTimeFormatter.ofPattern("MMM dd, HH:mm.ss"))
