package com.example.simpleweatherapp

import java.time.format.DateTimeFormatter
import java.util.*

object DateTimeFormatters {
    val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("H:mm")
    val dateFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("MMMM d", Locale("en", "US"))
    val dateTimeFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("MMMM d - H:mm", Locale("en", "US"))
    val dowFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("EEEE", Locale("en", "US"))
    val dowShortFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("EE", Locale("en", "US"))
}