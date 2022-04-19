package com.example.simpleweatherapp.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class BingMapsResponse(
    val authenticationResultCode: String,
    val brandLogoUri: String,
    val copyright: String,
    val resourceSets: Array<ResourceSet>,
    val statusCode: Int,
    val statusDescription: String,
    val traceId: String,
)

@JsonClass(generateAdapter = true)
class ResourceSet(
    val estimatedTotal: Int,
    val resources: Array<Resource>
)

@JsonClass(generateAdapter = true)
class Resource(
    val __type: String,
    val bbox: Array<Double>,
    val name: String,
    val point: Point,
    val address: Address,
    val confidence: String,
    val entityType: String,
    val geocodePoints: Array<GeocodePoint>,
    val matchCodes: Array<String>
)

@JsonClass(generateAdapter = true)
class Point(
    val type: String,
    val coordinates: Array<Double>,
)

@JsonClass(generateAdapter = true)
class Address(
    val addressLine: String?,
    val adminDistrict: String,
    val countryRegion: String,
    val formattedAddress: String,
    val locality: String,
    val adminDistrict2: String?
)

@JsonClass(generateAdapter = true)
class GeocodePoint(
    val type: String,
    val coordinates: Array<Double>,
    val calculationMethod: String,
    val usageTypes: Array<String>
)






