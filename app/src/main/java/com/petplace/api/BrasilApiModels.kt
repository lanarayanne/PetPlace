package com.petplace.api


data class BrasilApiResponse(
    val cep: String?,
    val state: String?,
    val city: String?,
    val neighborhood: String?,
    val street: String?,
    val service: String?,
    val location: BrasilApiLocation?
)

data class BrasilApiLocation(
    val type: String?,
    val coordinates: BrasilApiCoordinates?
)

data class BrasilApiCoordinates(
    val longitude: String?,
    val latitude: String?
)