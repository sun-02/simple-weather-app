package com.example.simpleweatherapp.util

import com.example.simpleweatherapp.model.openweather.UnitSystem
import java.util.*

fun Locale.toUnitSystem() =
    when (country.uppercase()) {
        "US", "GB", "MM", "LR" -> UnitSystem.IMPERIAL
        else -> UnitSystem.METRIC
    }