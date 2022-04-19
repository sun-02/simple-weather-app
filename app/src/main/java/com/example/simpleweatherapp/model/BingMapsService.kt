package com.example.simpleweatherapp.model

import com.example.simpleweatherapp.Config
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BingMapsService {
    @GET(Config.BING_MAPS_LOCATIONS_API_URL)
    suspend fun fetchLocations(
        @Query("locality") populatedPlace: String,
        @Query("key") bingMapsApiKey: String
    ): Response<Locations>
    //TODO Проверить как работает с KSP
}