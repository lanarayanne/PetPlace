package com.petplace.db.fb

import android.os.Build
import androidx.annotation.RequiresApi
import com.petplace.model.Age
import com.petplace.model.Animal
import com.petplace.model.Booking
import com.petplace.model.Hosting
import com.petplace.model.Pet
import com.petplace.model.Status
import com.petplace.model.User
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class FBBooking {
    var id: String? = null
    var host: FBUser? = null
    var client: FBUser? = null
    var pets: List<Pet>? = null
    var hosting: FBHosting? = null
    var value: String? = null
    var days: Int? = null
    var checkin: String? = null
    var checkout: String? = null
    var status: String? = null


//    fun toBooking() = Booking(id!!, host!!, client!!, pets!!, hosting = hosting?.toHosting()?: Hosting(), value = BigDecimal(value ?: "0.0"), days!!, checkin!!, checkout!!, status!! )
fun toBooking(): Booking {
    val safeStatus = try {
        if (status != null) Status.valueOf(status!!) else Status.PROXIMA
    } catch (e: Exception) {
        Status.PROXIMA
    }
    return Booking(
        id = id ?: "",
        host = host?.toUser() ?: User(),
        client = client?.toUser() ?: User(),
        hosting = hosting?.toHosting() ?: Hosting(),

        pets = pets ?: emptyList(),
        value = BigDecimal(value ?: "0.0"),
        days = days ?: 0,
        checkIn = checkin ?: "",
        checkOut = checkout ?: "",
        status = safeStatus
    )
}

}

fun Booking.toFBBooking(): FBBooking {
    val fb = FBBooking()
    fb.id = this.id

    fb.host = this.host.toFBUser()
    fb.client = this.client.toFBUser()
    fb.hosting = this.hosting.toFBHosting()

    fb.pets = this.pets
    fb.value = this.value.toString()
    fb.days = this.days
    fb.checkin = this.checkIn
    fb.checkout = this.checkOut
    fb.status = this.status.name

    return fb
}