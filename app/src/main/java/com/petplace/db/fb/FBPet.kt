package com.petplace.db.fb

import com.petplace.model.Age
import com.petplace.model.Animal
import com.petplace.model.Pet
import com.petplace.model.Color
import com.petplace.model.User

class FBPet {
    var id: Int = 0
    var name: String? = null
    var animal: Animal? = null
    var age: Age? = null
    var weight: Double = 0.0
    var birthYear: Int? = null
    var breed: String? = null
    var color: Color? = null
    var observations: String? = null
    var picture: String? = null

    fun toPet() = Pet(id, name!!, animal, age!!, birthYear, weight, breed, color, observations, picture  )

}

fun Pet.toFBPet() : FBPet {
    val fbPet = FBPet()
    fbPet.id = this.id
    fbPet.name = this.name
    fbPet.age = this.age
    fbPet.birthYear = this.birthYear
    fbPet.weight = this.weight
    fbPet.breed = this.breed
    fbPet.color = this.color
    fbPet.observations = this.observations
    fbPet.picture = this.picture

    return fbPet
}