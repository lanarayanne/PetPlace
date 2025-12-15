package com.petplace.db.fb

import com.petplace.model.Hosting
import com.petplace.model.Pet
import com.petplace.model.User

class FBUser {
    var name : String ? = null
    var email : String? = null

    var phone : String? =null
    var address: String? = null
    var password : String? = null
    var pet : List<Pet>? =null
    var hosting: List<Hosting>? =null

    fun toUser() = User(name!!, email!!, phone, address)
}

fun User.toFBUser() : FBUser {
    val fbUser = FBUser()
    fbUser.name = this.name
    fbUser.email = this.email
    fbUser.phone = this.phone
    fbUser.address = this.address
    return fbUser
}