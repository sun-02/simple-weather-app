package com.example.simpleweatherapp.data

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

class Converters {

    companion object {
        private const val secondsInDay = 86400L
    }

    private val zeroOffset = ZoneOffset.ofTotalSeconds(0)

    @TypeConverter
    fun toLocalDate(dateEpochSeconds: Int): LocalDate =
        LocalDate.ofEpochDay(dateEpochSeconds / secondsInDay)

    @TypeConverter
    fun fromLocalDate(date: LocalDate): Int = (date.toEpochDay() * secondsInDay).toInt()

    @TypeConverter
    fun toLocalTime(secondOfDay: Int): LocalTime = LocalTime.ofSecondOfDay(secondOfDay.toLong())

    @TypeConverter
    fun fromLocalTime(time: LocalTime): Int = time.toSecondOfDay()

    @TypeConverter
    fun toLocalDateTime(epochSecond: Int): LocalDateTime =
        LocalDateTime.ofEpochSecond(epochSecond.toLong(), 0, zeroOffset)

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime): Int = dateTime.toEpochSecond(zeroOffset).toInt()
}
