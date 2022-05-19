package com.example.simpleweatherapp.util

import android.util.ArrayMap
import com.example.simpleweatherapp.R

object ResourcesMapping {
    val weatherImagesRes = ArrayMap<String, Int>()
    val weatherIconsRes = ArrayMap<String, Int>()
    val uviIconsRes = ArrayMap<ClosedRange<Double>, Int>()
    val windDirectionsRes = ArrayMap<ClosedRange<Int>, Int>()

    init {
        weatherImagesRes.apply {
            put("01d", R.drawable.im_01d_clear_sky_336px)
            put("02d", R.drawable.im_02d_few_clouds_336px)
            put("03d", R.drawable.im_03d_scattered_clouds_336px)
            put("04d", R.drawable.im_04d_broken_clouds_336px)
            put("09d", R.drawable.im_09d_shower_rain_336px)
            put("10d", R.drawable.im_10d_rain_336px)
            put("11d", R.drawable.im_11d_thunderstorm_336px)
            put("13d", R.drawable.im_13d_snow_336px)
            put("50d", R.drawable.im_50d_mist_336px)
            put("01n", R.drawable.im_01n_clear_sky_336px)
            put("02n", R.drawable.im_02n_few_clouds_336px)
            put("03n", R.drawable.im_03n_scattered_clouds_336px)
            put("04n", R.drawable.im_04n_broken_clouds_336px)
            put("09n", R.drawable.im_09n_shower_rain_336px)
            put("10n", R.drawable.im_10n_rain_336px)
            put("11n", R.drawable.im_11n_thunderstorm_336px)
            put("13n", R.drawable.im_13n_snow_336px)
            put("50n", R.drawable.im_50n_mist_336px)
        }
        weatherIconsRes.apply {
            put("01d", R.drawable.ic_01d_96px)
            put("02d", R.drawable.ic_02d_96px)
            put("03d", R.drawable.ic_03d_96px)
            put("04d", R.drawable.ic_04d_96px)
            put("09d", R.drawable.ic_09d_96px)
            put("10d", R.drawable.ic_10d_96px)
            put("11d", R.drawable.ic_11d_96px)
            put("13d", R.drawable.ic_13d_96px)
            put("50d", R.drawable.ic_50d_96px)
            put("01n", R.drawable.ic_01n_96px)
            put("02n", R.drawable.ic_02n_96px)
            put("03n", R.drawable.ic_03n_96px)
            put("04n", R.drawable.ic_04n_96px)
            put("09n", R.drawable.ic_09n_96px)
            put("10n", R.drawable.ic_10n_96px)
            put("11n", R.drawable.ic_11n_96px)
            put("13n", R.drawable.ic_13n_96px)
            put("50n", R.drawable.ic_50n_96px)
        }
        uviIconsRes.apply {
            put(0.0..2.999, R.drawable.ic_uvi_1_2_24dp)
            put(3.0..5.999, R.drawable.ic_uvi_3_5_24dp)
            put(6.0..7.999, R.drawable.ic_uvi_6_7_24dp)
            put(8.0..10.999, R.drawable.ic_uvi_8_10_24dp)
            put(11.0..30.00, R.drawable.ic_uvi_11_24dp)
        }
        windDirectionsRes.apply {
            put(0..11, R.string.south)
            put(12..34, R.string.south_south_west)
            put(35..56, R.string.south_west)
            put(57..79, R.string.west_south_west)
            put(80..101, R.string.west)
            put(102..124, R.string.west_north_west)
            put(125..146, R.string.north_west)
            put(147..169, R.string.north_north_west)
            put(170..191, R.string.north)
            put(192..214, R.string.north_north_east)
            put(215..236, R.string.north_east)
            put(237..259, R.string.east_north_east)
            put(260..281, R.string.east)
            put(282..304, R.string.east_south_east)
            put(305..326, R.string.south_east)
            put(227..349, R.string.south_south_east)
            put(350..359, R.string.south)
        }
    }
}