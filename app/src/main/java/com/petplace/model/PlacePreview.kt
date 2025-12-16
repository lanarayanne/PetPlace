package com.petplace.model

import java.math.BigDecimal

data class PlacePreview (
    var id : String,
    val name : String,
    val evaluation : Int,
    val rating : Int,
    val distance : Double,
    val picture : String,
    val isFavorite : Boolean,
    val size : Double,
    val value : BigDecimal,
    val type : String,
    val dailyCount : Int,
    val petCount : Int

)