package com.example.simpleweatherapp.data.bingmaps

import com.example.simpleweatherapp.BuildConfig
import com.example.simpleweatherapp.Config
import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BingMapsService {

    @GET(Config.BING_MAPS_LOCATIONS_API_URL)
    suspend fun fetchLocationsByName(
        @Query("locality") populatedPlace: String,
        @Query("key") bingMapsApiKey: String = BuildConfig.BING_MAPS_KEY
    ): Response<List<ShortLocation>>

    @GET("${Config.BING_MAPS_LOCATIONS_API_URL}/{lat},{lon}")
    suspend fun fetchLocationByCoords(
        @Path("lat") latitude: Double,
        @Path("lon") longitude: Double,
        @Query("includeEntityTypes") includeEntityTypes: String = "PopulatedPlace",
        @Query("key") bingMapsApiKey: String = BuildConfig.BING_MAPS_KEY
    ): Response<List<ShortLocation>>
}