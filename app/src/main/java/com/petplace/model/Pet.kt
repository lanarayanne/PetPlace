package com.petplace.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import java.time.Year


data class Pet (
    val id : Int,
    val name : String,
    val animal : Animal,
    val age : Age,
    val birthYear: Int? =null,
    val weight: Double,
    val race : String? = null,
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
