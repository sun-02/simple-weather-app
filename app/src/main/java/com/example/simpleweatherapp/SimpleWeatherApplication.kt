package com.example.simpleweatherapp

import android.app.Application
import com.example.simpleweatherapp.data.DefaultMapsRepository
import com.example.simpleweatherapp.data.MapsRepository
import com.example.simpleweatherapp.data.remote.RemoteMapsDataSource
import com.example.simpleweatherapp.model.LocationsAdapter
import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class SimpleWeatherApplication : Application() {
    val mapsRepository: MapsRepository by lazy {
        val moshi = Moshi.Builder()
            .add(LocationsAdapter())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(Config.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        DefaultMapsRepository(RemoteMapsDataSource(retrofit))
    }
}