package com.example.simpleweatherapp.util

import com.example.simpleweatherapp.Const
import java.time.*

object DtUtil {
    // It's not using nanos!
    fun localTimeOfInstant(instant: Instant, zoneOffset: ZoneOffset): LocalTime {
        val localSecond = instant.epochSecond + zoneOffset.totalSeconds
        val secsOfDay = localSecond % Const.SECONDS_PER_DAY
        return LocalTime.ofSecondOfDay(secsOfDay)
    }

    fun localDateOfInstant(instant: Instant, zoneOffset: ZoneOffset): LocalDate {
        val localSecond = instant.epochSecond + zoneOffset.totalSeconds.toLong()
        val localEpochDay = localSecond / Const.SECONDS_PER_DAY
        return LocalDate.ofEpochDay(localEpochDay)
    }
}