package com.petplace.db.fb

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentId
import com.petplace.model.Hosting
import com.petplace.model.Pet
import com.petplace.model.User


class FBUser {
//    @DocumentId
    var id: String? = null

    var name : String? = null
    var email : String? = null
    var phone : String? = null
    var cep: String? = null
    var pets : List<Pet>? =null

    var lat : Double? = null
    var lng : Double? = null

    var address: String? = null



    fun toUser(): User {
        val locationObj = if (lat != null && lng != null) {
            LatLng(lat!!, lng!!)
        } else {
            null
        }

        return User(
            id = id ?: "",
            name = name ?: "",
            email = email ?: "",
            phone = phone,
            cep = cep,
            location = locationObj,
            address = address
        )
    }
}

fun User.toFBUser() : FBUser {
    val fbUser = FBUser()
    fbUser.id = this.id;
    fbUser.name = this.name
    fbUser.email = this.email
    fbUser.phone = this.phone
    fbUser.cep = this.cep
    fbUser.address = this.address
     fbUser.pets = this.pets
    fbUser.lat = this.location?.latitude
    fbUser.lng = this.location?.longitude

    return fbUser
}