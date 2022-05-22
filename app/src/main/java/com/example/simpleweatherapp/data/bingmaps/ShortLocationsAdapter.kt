package com.example.simpleweatherapp.data.bingmaps

import com.example.simpleweatherapp.model.bingmaps.BingMapsResponse
import com.example.simpleweatherapp.model.bingmaps.ResourceSet
import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class ShortLocationsAdapter {

    @FromJson fun fromBingMapsResponse(response: BingMapsResponse): List<ShortLocation> {
        val bingMapsLocations = response.resourceSets[0].resources
        val shortLocationList = mutableListOf<ShortLocation>()
        for (i in bingMapsLocations.indices) {
            shortLocationList.add(
                ShortLocation(
                    bingMapsLocations[i].name,
                    bingMapsLocations[i].address.locality,
                    bingMapsLocations[i].address.adminDistrict,
                    bingMapsLocations[i].address.countryRegion,
                    bingMapsLocations[i].point.coordinates[0],
                    bingMapsLocations[i].point.coordinates[1]
                )
            )
        }
        return shortLocationList
    }

    @ToJson fun stub(shortLocations: List<ShortLocation>): BingMapsResponse =
        BingMapsResponse(
            "",
            "",
            "",
            listOf(ResourceSet(0, listOf())),
            0,
            "",
            ""
        )
}
