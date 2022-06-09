package com.example.simpleweatherapp.data

import androidx.room.TypeConverter
import java.time.*

class Converters {

    @TypeConverter
    fun toInstant(epochSeconds: Long): Instant = Instant.ofEpochSecond(epochSeconds)

    @TypeConverter
    fun fromInstant(instant: Instant): Long = instant.epochSecond

    @TypeConverter
    fun toZoneOffset(epochSeconds: Int): ZoneOffset = ZoneOffset.ofTotalSeconds(epochSeconds)

    @TypeConverter
    fun fromZoneOffset(zoneOffset: ZoneOffset): Int = zoneOffset.totalSeconds
}
