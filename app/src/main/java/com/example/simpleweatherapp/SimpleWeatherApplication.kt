package com.example.simpleweatherapp

import android.app.Application
import com.example.simpleweatherapp.data.bingmaps.DefaultMapsRepository
import com.example.simpleweatherapp.data.bingmaps.MapsRepository
import com.example.simpleweatherapp.data.bingmaps.RemoteMapsDataSource
import com.example.simpleweatherapp.data.openweather.DefaultWeatherRepository
import com.example.simpleweatherapp.data.openweather.RemoteWeatherDataSource
import com.example.simpleweatherapp.data.openweather.WeatherRepository
import com.example.simpleweatherapp.data.bingmaps.ShortLocationsAdapter
import com.example.simpleweatherapp.data.openweather.OneCallWeatherAdapter
import com.google.android.gms.location.LocationServices
import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber

class SimpleWeatherApplication : Application() {
    val mapsRepository: MapsRepository by lazy {
        val moshi = Moshi.Builder()
            .add(ShortLocationsAdapter())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(Config.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        DefaultMapsRepository(RemoteMapsDataSource(retrofit))
    }

    val weatherRepository: WeatherRepository by lazy {
        val moshi = Moshi.Builder()
            .add(OneCallWeatherAdapter())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(Config.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        DefaultWeatherRepository(RemoteWeatherDataSource(retrofit))
    }

    val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}