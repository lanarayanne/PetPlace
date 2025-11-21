package com.petplace.model

import java.math.BigDecimal

data class Hosting (
    val name : String,
    val type : HostingType,
    val dailyRate : BigDecimal,
    val vacancies : Int,
    val address : String,
    val services : List<Service>,
    val description : String,
    val pictures : List<String>? =null
)
