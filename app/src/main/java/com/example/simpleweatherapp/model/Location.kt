package com.example.simpleweatherapp.model

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Location(
    val name: String,
    val populatedPlace: String,
    val adminDistrict: String,
    val countryRegion: String,
    val coords: Coords
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        Coords(parcel.readDouble(), parcel.readDouble())
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(populatedPlace)
        parcel.writeString(adminDistrict)
        parcel.writeString(countryRegion)
        parcel.writeDouble(coords.latitude)
        parcel.writeDouble(coords.longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Location> {
        override fun createFromParcel(parcel: Parcel): Location {
            return Location(parcel)
        }

        override fun newArray(size: Int): Array<Location?> {
            return arrayOfNulls(size)
        }
    }

}


data class Coords(val latitude: Double, val longitude: Double)

@JsonClass(generateAdapter = true)
data class Locations(val locations: List<Location>)