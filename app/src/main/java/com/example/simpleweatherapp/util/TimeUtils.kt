package com.example.simpleweatherapp.util

import java.time.LocalTime

const val SECONDS_IN_DAY = 86400L

fun getSafeLocalTime(epochSeconds: Int): LocalTime {
    return LocalTime.ofSecondOfDay((SECONDS_IN_DAY + epochSeconds) % SECONDS_IN_DAY)
}