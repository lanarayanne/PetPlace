package com.petplace.model

import com.google.android.gms.maps.model.LatLng

data class User (
    val name : String,
    val email : String,
    val phone : String? =null,
    //val address : LatLng? =null,
    val address: String? = null,
    val password : String? = null,
    val pets : List<Pet>? =null,
    val hosting: List<Hosting>? =null
)