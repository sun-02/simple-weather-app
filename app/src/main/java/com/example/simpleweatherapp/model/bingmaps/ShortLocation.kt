package com.example.simpleweatherapp.model.bingmaps

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "short_location")
@JsonClass(generateAdapter = true)
data class ShortLocation(
    @PrimaryKey val name: String,
    @ColumnInfo(name = "populated_place") val populatedPlace: String,
    @ColumnInfo(name = "admin_district") val adminDistrict: String,
    @ColumnInfo(name = "country_region") val countryRegion: String,
    val latitude: Double,
    val longitude: Double
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(populatedPlace)
        parcel.writeString(adminDistrict)
        parcel.writeString(countryRegion)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
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