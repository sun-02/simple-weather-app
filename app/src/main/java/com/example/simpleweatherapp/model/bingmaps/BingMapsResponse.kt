package com.example.simpleweatherapp.model.bingmaps

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BingMapsResponse(
    val authenticationResultCode: String,
    val brandLogoUri: String,
    val copyright: String,
    val resourceSets: List<ResourceSet>,
    val statusCode: Int,
    val statusDescription: String,
    val traceId: String,
)

@JsonClass(generateAdapter = true)
data class ResourceSet(
    val estimatedTotal: Int,
    val resources: List<Resource>
)

@JsonClass(generateAdapter = true)
data class Resource(
    val __type: String,
    val bbox: List<Double>,
    val name: String,
    val point: Point,
    val address: Address,
    val confidence: String,
    val entityType: String,
    val geocodePoints: List<GeocodePoint>,
    val matchCodes: List<String>
)

@JsonClass(generateAdapter = true)
data class Point(
    val type: String,
    val coordinates: List<Double>,
)

@JsonClass(generateAdapter = true)
data class Address(
    val addressLine: String?,
    val adminDistrict: String,
    val countryRegion: String,
    val formattedAddress: String,
    val locality: String,
    val adminDistrict2: String?
)

@JsonClass(generateAdapter = true)
data class GeocodePoint(
    val type: String,
    val coordinates: List<Double>,
    val calculationMethod: String,
    val usageTypes: List<String>
)






