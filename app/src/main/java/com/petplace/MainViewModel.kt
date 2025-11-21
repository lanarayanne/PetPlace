package com.petplace

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.petplace.model.Animal
import com.petplace.model.Color
import com.petplace.model.Hosting
import com.petplace.model.Pet
import com.petplace.model.PlacePreview
import com.petplace.model.User
import java.math.BigDecimal



class MainViewModel : ViewModel() {
    private val _hosting = getHostings().toMutableStateList()
    val hosting
        get() = _hosting.toList()

    val user = getUser()

}

fun getHostings() = List(3) { i ->
    PlacePreview(i, "Creche Patinhas", 245, 4,72.0, "Picture", false, 10.0, BigDecimal("145"), "Compartilhado", 1, 2)
}


fun getUser() = User(
    name = "Alana",
    email = "alana@gmail.com",
    phone = "(11)2480-2182",
    address = "Rua Um, nº154, Pimentas, Guarulhos - SP",
    password = "senha123"
)