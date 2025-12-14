package com.petplace.model

import com.google.android.gms.maps.model.LatLng
import java.math.BigDecimal

data class Hosting (
    val name : String,
    val type : HostingType,
    val dailyRate : BigDecimal,
    val vacancies : Int,
    val location : LatLng? = null,
    val complement : String? = null,
    val services : List<Service>,
    val description : String,
    val pictures : List<String>? =null
)

enum class HostingType(val descricao: String) {
    INDIVIDUAL("Quarto individual"),
    COMPARTILHADO("Compartilhado")
}