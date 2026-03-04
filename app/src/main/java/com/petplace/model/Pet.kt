package com.petplace.model


data class Pet (
    val id : Int=0,
    var name : String="",
    var animal : String?=null, //TODO: Change
    var age : Age = Age.DESCONHECIDO,
    var birthYear: Int? =null,
    var weight: Double = 0.0,
    var breed : String? = null,
    var color : String? = null,
    var observations : String? =null,
    var picture : String? =null
)

enum class Age(val faixaEtaria: String) {
    FILHOTE("Filhote"),
    ADULTO("Adulto"),
    IDOSO("Idoso"),
    DESCONHECIDO("Desconhecido")
}
