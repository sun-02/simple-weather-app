package com.example.simpleweatherapp.data.bingmaps

import androidx.lifecycle.LiveData
import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import com.example.simpleweatherapp.model.Result

interface MapsRepository {
    suspend fun addFavLocation(sLocation: ShortLocation)
    suspend fun removeFavLocation(name: String)
    fun observeFavLocationList(): LiveData<Result<List<ShortLocation>>>
    suspend fun getRemoteLocationList(name: String): Result<List<ShortLocation>>
    suspend fun getRemoteLocation(latitude: Double, longitude: Double): Result<List<ShortLocation>>
}
