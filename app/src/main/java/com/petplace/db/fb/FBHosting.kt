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
    var lng: Double? = null
    var cep: String? = null
    var address: String? = null
    var complement: String? = null
    var services: List<Service>? = null
    var description: String? = null
    var pictures: List<String>? = null
    var rating: Double? = null
    var reviewsCount: Int? = null
    var owner: FBUser? = null
    var size: Double? = 0.0

    fun toHosting(): Hosting {
        val locationObj = if (lat != null && lng != null) {
            LatLng(lat!!, lng!!)
        } else {
            null
        }

        return Hosting(
            id = id ?: "",
            name = name ?: "Sem Nome",
            type = type?: HostingType.COMPARTILHADO,
            dailyRate = BigDecimal(dailyRate ?: "0.0"),
            vacancies = vacancies ?: 0,
            cep = cep?: "",
            address = address,
            location = locationObj,
            complement = complement ?: "",
            services = services ?: emptyList(),
            description = description ?: "",
            pictures = pictures ?: emptyList(),
            rating = rating ?: 0.0,
            reviewsCount = reviewsCount ?: 0,
            owner = owner?.toUser(),
            size = size?: 0.0
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
    fbHosting.lng = this.location?.longitude
    fbHosting.cep = this.cep
    fbHosting.address = this.address
    fbHosting.complement = this.complement
    fbHosting.services = this.services
    fbHosting.description = this.description
    fbHosting.pictures = this.pictures
    fbHosting.rating = this.rating
    fbHosting.reviewsCount = this.reviewsCount
    fbHosting.owner = this.owner?.toFBUser()
    fbHosting.size = this.size

    return fbHosting
}