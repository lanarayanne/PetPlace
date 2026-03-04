package com.petplace.model

import java.math.BigDecimal

data class PlacePreview (
    var id : String,
    val name : String,
    val picture : String? = null,
    val size : Double? = 0.0,
    val value : BigDecimal,
    val type : String,
    val vacancies: Int,

    val evaluation : Int,
    val rating : Int,

    val distance : Double? =null,
    val isFavorite : Boolean,

    val dailyCount : Int? = null,
    val petCount : Int? = null,

    val lat: Double? = null,
    val lng: Double? = null

)