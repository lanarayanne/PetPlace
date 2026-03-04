package com.petplace.model

import com.google.android.gms.maps.model.LatLng

data class User (
    val id: String = "",
    val name : String = "",
    val email : String = "",
    val phone : String? = null,
    var cep : String? = null,
    val location : LatLng? =null,
    val address: String? = "",
    val password : String? = null,
    val pets : List<Pet>? =null,
    val hosting: List<Hosting>? =null
)