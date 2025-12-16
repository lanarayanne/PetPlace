package com.petplace.db.fb

import com.google.android.gms.maps.model.LatLng
import com.petplace.model.Booking
import com.petplace.model.Hosting
import com.petplace.model.HostingType
import com.petplace.model.Pet
import com.petplace.model.Service
import com.petplace.model.Status
import com.petplace.model.User
import java.math.BigDecimal

class FBHosting {
    var id: String? = ""
    var name: String? = null
    var type: HostingType? = HostingType.COMPARTILHADO
    var dailyRate : String? = null
    var vacancies: Int? = null
    var lat: Double? = null
    var lgn: Double? = null
    var address: String? = null
    var location: String? = null
    var complement: String? = null
    var services: List<Service>? = null
    var description: String? = null
    var pictures: List<String>? = null
    var rating: Double? = null
    var reviewsCount: Int? = null
    var owner: User? = null

    fun toHosting(): Hosting {
        val locationObj = if (lat != null && lgn != null) {
            LatLng(lat!!, lgn!!)
        } else {
            null
        }

        return Hosting(
            id = id ?: "",
            name = name ?: "Sem Nome",
            type = type?: HostingType.COMPARTILHADO,
            dailyRate = BigDecimal(dailyRate ?: "0.0"),
            vacancies = vacancies ?: 0,
            location = locationObj,
            complement = complement ?: "",
            services = services ?: emptyList(),
            description = description ?: "",
            pictures = pictures ?: emptyList(),
            rating = rating ?: 0.0,
            reviewsCount = reviewsCount ?: 0,
            owner = owner ?: User()
        )
    }
}

fun Hosting.toFBHosting() : FBHosting {
    val fbHosting = FBHosting()
    fbHosting.id = this.id
    fbHosting.name = this.name
    fbHosting.type = this.type
    fbHosting.vacancies = this.vacancies
    fbHosting.dailyRate = this.dailyRate.toString()
    fbHosting.lat = this.location?.latitude
    fbHosting.lgn = this.location?.longitude
    fbHosting.complement = this.complement
    fbHosting.services = this.services
    fbHosting.description = this.description
    fbHosting.pictures = this.pictures
    fbHosting.rating = this.rating
    fbHosting.reviewsCount = this.reviewsCount
    fbHosting.owner = this.owner

    return fbHosting
}