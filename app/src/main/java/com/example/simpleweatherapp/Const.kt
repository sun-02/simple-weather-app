package com.example.simpleweatherapp

object Const {
    const val WEATHER_REFRESH_INTERVAL = 10 * 60 * 1000L
    const val WEATHER_CLEANUP_INTERVAL = 24 * 60 * 60
    const val LOCATION_REFRESH_INTERVAL = 30 * 60 * 1000L
    const val LAST_LOCATION_KEY = "LAST_LOCATION_KEY"
    const val LAST_WEATHER_CLEANUP = "LAST_WEATHER_CLEANUP"
    const val DEGREE_TO_LEVEL_COEF = 27.778
}