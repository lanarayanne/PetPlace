package com.petplace.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import java.time.Year


data class Pet (
    val id : Int = 0,
    val name : String = "",
    val animal : Animal? = null, //TODO: Change
    val age : Age = Age.DESCONHECIDO,
    val birthYear: Int? =null,
    val weight: Double = 0.0,
    val breed : String? = null,
    val color : com.petplace.model.Color? = null,
    val observations : String? =null,
    val picture : String? =null
)

enum class Age(val faixaEtaria: String) {
    FILHOTE("Filhote"),
    ADULTO("Adulto"),
    IDOSO("Idoso"),
    DESCONHECIDO("Desconhecido")
}
