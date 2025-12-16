package com.petplace.db.fb

import android.os.Build
import androidx.annotation.RequiresApi
import com.petplace.model.Age
import com.petplace.model.Animal
import com.petplace.model.Booking
import com.petplace.model.Color
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
    var id: String? = ""
    var host: User? = null
    var client: User? = null
    var pets : List<Pet>? = null
    var hosting: FBHosting? = null
    var value: String? = null
    var days: Int? = null
    var checkin: String? = null
    var checkout: String? = null
    var status: String? = null


    //fun toBooking() = Booking(id!!, host!!, client!!, pets!!, hosting = hosting?.toHosting()?: Hosting(), value = BigDecimal(value ?: "0.0"), days!!, checkin!!, checkout!!, status!! )
    fun toBooking(): Booking {
        val storedStatus = try {
            Status.valueOf(status ?: Status.PROXIMA.name)
        } catch (e: Exception) {
            Status.PROXIMA
        }

        val finalStatus = if (storedStatus == Status.CANCELADA) {
            Status.CANCELADA
        } else {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)

                val checkInDate = sdf.parse(checkin ?: "")
                val checkOutDate = sdf.parse(checkout ?: "")

                val hojeComHoras = Date()
                val hojeStr = sdf.format(hojeComHoras)
                val hoje = sdf.parse(hojeStr)

                if (checkInDate != null && checkOutDate != null && hoje != null) {
                    when {
                        hoje.before(checkInDate) -> Status.PROXIMA
                        (hoje == checkInDate || hoje.after(checkInDate)) && (hoje == checkOutDate || hoje.before(checkOutDate)) -> Status.EMANDAMENTO
                        hoje.after(checkOutDate) -> Status.CONCLUIDA
                        else -> storedStatus
                    }
                } else {
                    storedStatus
                }
            } catch (e: Exception) {
                storedStatus
            }
        }

        return Booking(
            id = id ?: "",
            host = host ?: User(),
            client = client ?: User(),
            pets = pets ?: emptyList(),
            hosting = hosting?.toHosting() ?: Hosting(),
            value = java.math.BigDecimal(value ?: "0.0"),
            days = days ?: 0,
            checkIn = checkin ?: "",
            checkOut = checkout ?: "",
            status = finalStatus
        )
    }

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
    fbBooking.status = this.status.name

    return fbBooking
}