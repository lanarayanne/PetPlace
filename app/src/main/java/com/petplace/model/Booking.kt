package com.petplace.model

import java.math.BigDecimal
import java.util.Date

data class Booking (
    val id : Integer,
    val host : User,
    val client : User,
    val pet : List<Pet>,
    val hosting : Hosting,
    val value : BigDecimal,
    val days : Integer,
    val checkIn : Date,
    val checkOut : Date

)