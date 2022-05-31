package com.example.simpleweatherapp.data.bingmaps

import com.example.simpleweatherapp.data.LocalDataSource
import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import com.example.simpleweatherapp.model.Result

class DefaultMapsRepository(
    private val remoteMapsDataSource: RemoteMapsDataSource,
    private val localDataSource: LocalDataSource
) : MapsRepository {

    override suspend fun addFavLocation(sLocation: ShortLocation) =
        localDataSource.addFavLocation(sLocation)

    override suspend fun removeFavLocation(name: String) =
        localDataSource.deleteFavLocation(name)

    override suspend fun getFavLocationList(): Result<List<ShortLocation>> =
        localDataSource.getFavLocationList()

    override suspend fun getRemoteLocationList(name: String): Result<List<ShortLocation>> {
        return remoteMapsDataSource.fetchLocations(name)
    }

    override suspend fun getRemoteLocation(
        latitude: Double,
        longitude: Double
    ): Result<List<ShortLocation>> {
        return remoteMapsDataSource.fetchLocation(latitude, longitude)
    }
}
