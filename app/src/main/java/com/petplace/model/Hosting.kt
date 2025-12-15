package com.petplace.model

import com.google.android.gms.maps.model.LatLng
import java.math.BigDecimal

data class Hosting (
    val id : Int,
    val name : String,
    val type : HostingType,
    val dailyRate : BigDecimal,
    val vacancies : Int,
    val location : LatLng? = null,
    val complement : String? = null,
    val services : List<Service>,
    val description : String,
    val pictures : List<String>? =null,

    val rating: Double = 0.0,
    val reviewsCount: Int = 0,
    val ownerName: String = ""
)

enum class HostingType(val descricao: String) {
    INDIVIDUAL("Quarto individual"),
    COMPARTILHADO("Compartilhado")
}