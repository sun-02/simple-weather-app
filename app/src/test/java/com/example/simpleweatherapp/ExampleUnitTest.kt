package com.example.simpleweatherapp

import org.junit.Test

import org.junit.Assert.*

import com.example.simpleweatherapp.Config
import com.example.simpleweatherapp.model.bingmaps.BingMapsResponse
import com.example.simpleweatherapp.model.bingmaps.BingMapsService
import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import com.example.simpleweatherapp.model.bingmaps.ShortLocationsAdapter
import com.example.simpleweatherapp.model.openweather.OneCallWeather
import com.example.simpleweatherapp.model.openweather.OneCallWeatherAdapter
import com.example.simpleweatherapp.model.openweather.OpenWeatherService
import com.example.simpleweatherapp.model.openweather.UnitSystem
import com.squareup.moshi.Moshi
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun retrofitRequestToMapsByCoords() {
        val (latitude, longitude) = Pair(47.64054,-122.12934)
        val moshi: Moshi = Moshi.Builder()
            .add(ShortLocationsAdapter())
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Config.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        val service: BingMapsService = retrofit.create(BingMapsService::class.java)

        var result: Response<List<ShortLocation>>? = null
        runBlocking {
            result = service.fetchLocationByCoords(latitude, longitude)
            println(result!!.body())
        }
    }

    @Test
    fun retrofitRequestToOneCallWeather() {
        val (latitude, longitude) = Pair(47.64054,-122.12934)
        val moshi: Moshi = Moshi.Builder()
            .add(OneCallWeatherAdapter())
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Config.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        val service: OpenWeatherService = retrofit.create(OpenWeatherService::class.java)

        var result: Response<OneCallWeather>? = null
        runBlocking {
            result = service.fetchOneCallWeather(latitude, longitude, UnitSystem.METRIC)
            println(result!!.body())
        }
    }
}