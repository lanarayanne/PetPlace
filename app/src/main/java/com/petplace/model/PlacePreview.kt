package com.petplace.model

import java.math.BigDecimal

data class PlacePreview (
    var id : String,
    val name : String,
    val picture : String? = null,
    val size : Double,
    val value : BigDecimal,
    val type : String,
    val vacancies: Int,

    val evaluation : Int,
    val rating : Int,

    val distance : Double,
    val isFavorite : Boolean,

    val dailyCount : Int,
    val petCount : Int,

    val lat: Double? = null,
    val lng: Double? = null

)