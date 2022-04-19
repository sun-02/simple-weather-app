package com.example.simpleweatherapp.model

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class LocationsAdapter() {

    @FromJson fun fromBingMapsResponse(response: BingMapsResponse): Locations {
        val locationRes = response.resourceSets[0].resources
        val locations = mutableListOf<Location>()
        for (i in locationRes.indices) {
            locations.add(
                Location(
                    locationRes[i].name,
                    locationRes[i].address.locality,
                    locationRes[i].address.adminDistrict,
                    locationRes[i].address.countryRegion,
                    Coords(locationRes[i].point.coordinates[0],
                        locationRes[i].point.coordinates[1]
                    )
                )
            )
        }
        return Locations(locations)
    }

    @ToJson fun dummy(locations: Locations): BingMapsResponse =
        BingMapsResponse(
            "",
            "",
            "",
            arrayOf(ResourceSet(0, emptyArray())),
            0,
            "",
            ""
        )
}
