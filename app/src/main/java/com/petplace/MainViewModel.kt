package com.petplace

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.petplace.db.fb.FBDatabase
import com.petplace.db.fb.FBUser
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import com.petplace.model.SearchCriteria


class MainViewModel (private val db: FBDatabase) : ViewModel(), FBDatabase.Listener {
    private val _hosting = getHostingPreview().toMutableStateList()

    val hosting
        get() = _hosting.toList()

    private val allHostingsMock = getMockHostingsList() //TODO

    private val _user = mutableStateOf<User?> (null)

    val user : User?
        get() = _user.value

    val pets = getPetsList()

    private val _booking = getBookings().toMutableStateList()
    val booking
        get() = _booking.toList()

    val hostingPreviews = mutableStateListOf<PlacePreview>()

    var selectedHosting by mutableStateOf<Hosting?>(null)
        private set

    var currentSearch by mutableStateOf(SearchCriteria())
        private set

    // Função para atualizar a busca (usada na Home)
    fun updateSearch(criteria: SearchCriteria) {
        currentSearch = criteria
    }

    init {
        db.setListener(this)
    }

    override fun onUserLoaded(user: FBUser) {
        _user.value = user.toUser()

        val userId = user.toUser().id
        if (userId.isNotEmpty()) {
            db.startPetsListener(userId) { petsDoBanco ->

                val currentUser = _user.value
                if (currentUser != null) {
                    _user.value = currentUser.copy(pets = petsDoBanco)
                }
            }
        }
    }

    override fun onUserSignOut() {
        //TODO("Not yet implemented")
    }

    fun selectHostingById(id: Int) {
        selectedHosting = allHostingsMock.find { it.id == id }
    }

    fun saveNewPet(
        name: String,
        animalType: String,
        breed: String?,
        age: Age,
        weight: Double,
        birthYear: Int?,
        colorName: String?,
        observations: String?,
        onSuccess: () -> Unit
    ) {
        val currentUser = _user.value ?: return

        val newPet = Pet(
            id = (System.currentTimeMillis() % 100000).toInt(),
            name = name,
            animal = Animal(animalType), // TODO:
            age = age,
            weight = weight,
            birthYear = birthYear,
            breed = breed,
            color = null, // TODO:
            observations = observations,
            picture = null
        )

        val updatedPets = currentUser.pets?.toMutableList()

        updatedPets?.add(newPet)

        _user.value = currentUser.copy(pets = updatedPets)

        db.savePet(
            userId = currentUser.id,
            pet = newPet,
            onSuccess = {

                val updatedPets = currentUser.pets?.toMutableList()
                updatedPets?.add(newPet)
                _user.value = currentUser.copy(pets = updatedPets)

                onSuccess()
            },
            onFailure = { e ->
                e.printStackTrace()
            }
        )
    }



}

class MainViewModelFactory(private val db : FBDatabase) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(db) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


fun getHosting() = Hosting(
    id = 1,
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



fun getUser() = User(
    id = "1548465",
    name = "Alana",
    email = "alana@gmail.com",
    phone = "(11)2480-2182",
    address = null,
    password = "senha123"
)


fun getPetsList() = listOf(
    Pet(1,"Agata", Animal("Gato"), Age.ADULTO, 2020, 5.3, "SRD", Color.PRETO),
    Pet(2, "Mia", Animal("Gato"), Age.ADULTO, 2023, 3.5, "SRD", Color.TRICOLOR),
    Pet(3, "Rita", Animal("Gato"), Age.ADULTO, 2020, 4.7, "SRD", Color.MESCLADO),
    Pet(4, "Diego", Animal("Gato"), Age.ADULTO, null, 4.4, "SRD", Color.LARANJA)
)

fun getHostingPreview() = List(5) { i ->
    PlacePreview(
        id = i, // IDs serão 0, 1, 2, 3, 4
        name = "Creche Patinhas $i",
        distance = 245.0 + i,
        rating = 4,
        evaluation = 3,
        picture = "Picture",
        isFavorite = false,
        size = 10.0 + i,
        value = BigDecimal(145 + i),
        type = "Compartilhado",
        dailyCount = 1,
        petCount = 2
    )
}

fun getMockHostingsList() = List(5) { i ->
    Hosting(
        id = i, // O ID TEM QUE BATER com o Preview (0, 1, 2...)
        name = "Creche Patinhas $i",
        type = if (i % 2 == 0) HostingType.COMPARTILHADO else HostingType.INDIVIDUAL,
        dailyRate = BigDecimal(145.00 + i),
        vacancies = 15 - i,
        location = null,
        complement = "Próximo ao parque central",
        services = listOf(
            Service("Alimentação"),
            Service("Ar-Condicionado"),
            Service("Passeio Diário"),
            Service("Banho")
        ),
        description = "Esta é a descrição detalhada da hospedagem número $i. " +
                "Aqui seu pet terá todo o conforto e carinho que merece. " +
                "Ambiente climatizado e monitorado 24 horas.",
    )
}

fun getBookings() = List(3) { i ->
    Booking(
        id = i,
        client = getUser(),
        host = getUser(),
        pets = getPetsList(),
        hosting = getMockHostingsList()[0],
        value = BigDecimal(520.00),
        days = 5,
        checkIn = "10/12/2025",
        checkOut = "15/12/2025",
        status = Status.PROXIMA
    )
}
