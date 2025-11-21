package com.petplace.model

import java.math.BigDecimal
import java.util.Date

data class Booking (
    val id : Int,
    val host : User,
    val client : User,
    val pet : List<Pet>,
    val hosting : Hosting,
    val value : BigDecimal,
    val days : Int,
    val checkIn : String,
    val checkOut : String,
    val status : Status

)

enum class Status(val status: String) {
    PROXIMA("Próxima"),
    EMANDAMENTO("Em Andamento"),
    CONCLUIDA("Concluída"),
    CANCELADA("Cancelada")


}