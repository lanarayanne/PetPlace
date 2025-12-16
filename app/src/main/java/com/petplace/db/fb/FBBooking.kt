package com.petplace.db.fb

import com.petplace.model.Age
import com.petplace.model.Animal
import com.petplace.model.Booking
import com.petplace.model.Color
import com.petplace.model.Hosting
import com.petplace.model.Pet
import com.petplace.model.Status
import com.petplace.model.User
import java.math.BigDecimal

class FBBooking {
    var id: String? = ""
    var host: User? = null
    var client: User? = null
    var pets : List<Pet>? = null
    var hosting: FBHosting? = null
    var value: String? = null
    var days: Int? = null
    var checkin: String? = null
    var checkout: String? = null
    var status: Status? = null


    fun toBooking() = Booking(id!!, host!!, client!!, pets!!, hosting = hosting?.toHosting()?: Hosting(), value = BigDecimal(value ?: "0.0"), days!!, checkin!!, checkout!!, status!! )

}

fun Booking.toFBBooking() : FBBooking {
    val fbBooking = FBBooking()
    fbBooking.id = this.id
    fbBooking.host = this.host
    fbBooking.client = this.client
    fbBooking.pets = this.pets
    fbBooking.value = this.value.toString()
    fbBooking.hosting = this.hosting.toFBHosting()
    fbBooking.days = this.days
    fbBooking.checkout = this.checkOut
    fbBooking.checkin = this.checkIn
    fbBooking.status = this.status

    return fbBooking
}