package com.petplace.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import java.time.Year


data class Pet (
    val name : String,
    val animal : Animal,
    val age : Integer,
    val birthYear: Year,
    val weight: Double,
    val race : String? = null,
    val color : com.petplace.model.Color? = null,
    val observations : String? =null

)