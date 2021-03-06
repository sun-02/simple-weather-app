package com.example.simpleweatherapp

object Const {
    const val SECONDS_PER_DAY = 24 * 60 * 60L
    const val WEATHER_REFRESH_INTERVAL = 10 * 60 * 1000L
    const val WEATHER_CLEANUP_INTERVAL = SECONDS_PER_DAY * 1000L
    const val LOCATION_REFRESH_INTERVAL = 5 * 60 * 1000L
    const val LAST_LOCATION_KEY = "LAST_LOCATION_KEY"
    const val CURRENT_LOCATION_KEY = "CURRENT_LOCATION_KEY"
    const val LAST_WEATHER_CLEANUP = "LAST_WEATHER_CLEANUP"
    const val ROTATION_ANGLE_TO_LEVEL_COEF = 27.778
}