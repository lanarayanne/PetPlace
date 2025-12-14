package com.petplace

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.petplace.model.Age
import com.petplace.model.Animal
import com.petplace.model.Booking
import com.petplace.model.Color
import com.petplace.model.Hosting
import com.petplace.model.HostingType
import com.petplace.model.Pet
import com.petplace.model.PlacePreview
import com.petplace.model.Service
import com.petplace.model.Status
import com.petplace.model.User
import java.math.BigDecimal
import kotlin.String


class MainViewModel : ViewModel() {
    private val _hosting = getHostingPreview().toMutableStateList()

    val hosting
        get() = _hosting.toList()

    val user = getUser()

    val pets = getPetsList()

    private val _booking = getBookings().toMutableStateList()
    val booking
        get() = _booking.toList()

}



fun getHostingPreview() = List(3) { i ->
    PlacePreview(i, "Creche Patinhas", 245, 4,72.0, "Picture", false, 10.0, BigDecimal("145"), "Compartilhado", 1, 2)
}

fun getHosting() = Hosting(
    name = "Creche Patinhas",
    type = HostingType.COMPARTILHADO,
    dailyRate = BigDecimal(145.00),
    vacancies = 15,
    location = null,
    complement = null,
    services = listOf(
        Service("Alimentação"),
        Service("Ar-Condicionado")
    ),
    description = "descrição",
)

fun getBookings() = List(3) { i ->
    Booking(i, getUser(), getUser(), getPetsList(), getHosting(), BigDecimal(520.00), 5, "10/12/2025", "15/12/2025", status = Status.PROXIMA )
}


fun getUser() = User(
    name = "Alana",
    email = "alana@gmail.com",
    phone = "(11)2480-2182",
    address = "Rua Um, nº154, Pimentas, Guarulhos - SP",
    password = "senha123"
)


fun getPetsList() = listOf(
    Pet(1,"Agata", Animal("Gato"), Age.ADULTO, 2020, 5.3, "SRD", Color.PRETO),
    Pet(2, "Mia", Animal("Gato"), Age.ADULTO, 2023, 3.5, "SRD", Color.TRICOLOR),
    Pet(3, "Rita", Animal("Gato"), Age.ADULTO, 2020, 4.7, "SRD", Color.MESCLADO),
    Pet(4, "Diego", Animal("Gato"), Age.ADULTO, null, 4.4, "SRD", Color.LARANJA)
)