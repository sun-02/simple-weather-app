package com.example.simpleweatherapp.data.bingmaps

import androidx.lifecycle.LiveData
import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import com.example.simpleweatherapp.model.Result

interface MapsRepository {
    suspend fun addFavLocation(sLocation: ShortLocation)
    suspend fun removeFavLocation(sLocation: ShortLocation)
    fun observeFavLocationList(): LiveData<List<ShortLocation>>
    suspend fun getRemoteLocationList(name: String): Result<List<ShortLocation>>
    suspend fun getRemoteLocation(latitude: Double, longitude: Double): Result<List<ShortLocation>>
}
