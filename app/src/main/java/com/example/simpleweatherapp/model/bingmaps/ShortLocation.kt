package com.example.simpleweatherapp.model.bingmaps

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ShortLocation(
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

    companion object CREATOR : Parcelable.Creator<ShortLocation> {
        override fun createFromParcel(parcel: Parcel): ShortLocation {
            return ShortLocation(parcel)
        }

        override fun newArray(size: Int): Array<ShortLocation?> {
            return arrayOfNulls(size)
        }
    }
}

@JsonClass(generateAdapter = true)
data class Coords(val latitude: Double, val longitude: Double)