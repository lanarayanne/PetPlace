package com.petplace.model

import android.location.Address
import com.google.android.gms.maps.model.LatLng
import java.math.BigDecimal

data class Hosting (
    val id : String = "",
    val name : String = "",
    val type : HostingType = HostingType.COMPARTILHADO,
    val dailyRate : BigDecimal = BigDecimal.ZERO,
    val vacancies : Int = 0,
    val address: String? = null,
    val location : LatLng? = null,
    val complement : String? = null,
    val services : List<Service> = emptyList(),
    val description : String = "",
    val pictures : List<String>? =null,
    val rating: Double = 0.0,
    val reviewsCount: Int = 0,
    val owner: User? = null
)

enum class HostingType(val descricao: String) {
    INDIVIDUAL("Quarto individual"),
    COMPARTILHADO("Compartilhado")
}