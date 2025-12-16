package com.petplace

import android.location.Geocoder
import android.util.Log
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
import com.google.android.gms.maps.model.LatLng
import android.content.Context
import android.location.Location
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.petplace.model.SearchCriteria
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException


class MainViewModel (private val db: FBDatabase) : ViewModel(), FBDatabase.Listener {
    private val _hostings = getMockHostingsList().toMutableStateList()

    val hostings: List<Hosting>
        get() = _hostings

    private val _user = mutableStateOf<User?> (null)

    val user : User?
        get() = _user.value

    private val _bookings = mutableStateListOf<Booking>()
    val booking: List<Booking>
        get() = _bookings

    val hostingPreviews = mutableStateListOf<PlacePreview>()

    var selectedHosting by mutableStateOf<Hosting?>(null)
        private set

    var currentSearch by mutableStateOf(SearchCriteria())
        private set

    //private val _searchResults = mutableStateListOf<PlacePreview>()
//
//    val searchResults: List<PlacePreview>
//        get() = _searchResults

    var searchResults by mutableStateOf<List<PlacePreview>>(emptyList())
        private set

    var deviceLocation by mutableStateOf<LatLng?>(null)
        private set


    var favoriteIds by mutableStateOf<Set<String>>(emptySet())
        private set

    fun updateDeviceLocation(location: LatLng) {
        deviceLocation = location
        performSearch()
    }

    var hasSearched by mutableStateOf(false)
        private set

    fun isFavorite(hostingId: String): Boolean {
        return favoriteIds.contains(hostingId)
    }

    fun onSearchClicked() {
        hasSearched = true
        performSearch()
    }

    fun updateSearch(criteria: SearchCriteria) {
        currentSearch = criteria
    }

    fun toggleFavorite(placeId: String) {
        val currentUser = user ?: return

        val isCurrentlyFavorite = favoriteIds.contains(placeId)

        db.toggleFavorite(
            userId = currentUser.id,
            hostingId = placeId,
            isFavorite = !isCurrentlyFavorite
        )
    }



    init {
        db.setListener(this)
        performSearch()
    }

    suspend fun getCoordinatesFromAddress(context: Context, address: String): LatLng? {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context)
                val results = geocoder.getFromLocationName(address, 1)

                if (!results.isNullOrEmpty()) {
                    val location = results[0]
                    LatLng(location.latitude, location.longitude)
                } else {
                    null
                }
            } catch (e: IOException) {
                Log.e("Geocoding", "Erro de conexão ou endereço inválido: ${e.message}")
                null
            } catch (e: Exception) {
                Log.e("Geocoding", "Erro genérico: ${e.message}")
                null
            }
        }
    }

    override fun onUserLoaded(user: FBUser) {
        _user.value = user.toUser()
        val userId = user.toUser().id

        db.startFavoritesListener(userId) { ids ->
            favoriteIds = ids.toSet()
        }

        db.startPetsListener(userId) { petsDoBanco ->
            val currentUser = _user.value
            if (currentUser != null) {
                _user.value = currentUser.copy(pets = petsDoBanco)
            }
        }

        // reservas
        db.startBookingsListener(userId) { reservasDoBanco ->
            _bookings.clear()
            _bookings.addAll(reservasDoBanco)
        }
    }



    override fun onUserSignOut() {
        //TODO("Not yet implemented")
    }

    fun selectHostingById(id: String) {
        selectedHosting = _hostings.find { it.id == id }
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

    fun saveBooking(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val hosting = selectedHosting
        val search = currentSearch
        val currentUser = user

        if (hosting == null || currentUser == null) {
            onError("Erro: Falta usuário ou hospedagem.")
            return
        }

        val bookingId = java.util.UUID.randomUUID().toString()

        val newBooking = com.petplace.model.Booking(
            id = bookingId,
            host = com.petplace.model.User("12345", name = "Anfitrião Teste", "a@gmail.com"), // TODO:
            client = currentUser,
            pets = emptyList(),
            hosting = hosting,
            value = search.value, // Double
            days = 1,
            checkIn = search.startDate.toString(),
            checkOut = search.endDate.toString(),
            status = com.petplace.model.Status.PROXIMA
        )

        db.saveBooking(
            booking = newBooking,
            onSuccess = { onSuccess() },
            onFailure = { e -> onError(e.message ?: "Erro ao salvar") }
        )
    }

    private fun calculateDistanceKm(
        from: LatLng?,
        to: LatLng?
    ): Double {
        if (from == null || to == null) return Double.MAX_VALUE

        val results = FloatArray(1)
        Location.distanceBetween(
            from.latitude, from.longitude,
            to.latitude, to.longitude,
            results
        )
        return results[0] / 1000.0
    }


    fun performSearch() {
        val currentUser = user
        val myLocation = deviceLocation
        val searchCriteria = currentSearch

        val userPetsCount = currentUser?.pets?.size ?: 1
        val days = if (searchCriteria.dailyRates > 0) searchCriteria.dailyRates else 1

        val results = _hostings.mapNotNull { hosting ->

            val distanceKm =
                if (myLocation != null)
                    calculateDistanceKm(myLocation, hosting.location)
                else
                    0.0

            if (myLocation != null && distanceKm > 30) return@mapNotNull null
            if (hosting.vacancies < userPetsCount) return@mapNotNull null

            PlacePreview(
                id = hosting.id,
                name = hosting.name,
                lat = hosting.location?.latitude,
                lng = hosting.location?.longitude,
                value = hosting.dailyRate,
                type = hosting.type.descricao,
                vacancies = hosting.vacancies,
                petCount = userPetsCount,
                dailyCount = days,
                distance = distanceKm,
                rating = hosting.rating.toInt(),
                evaluation = hosting.reviewsCount,
                picture = null,
                isFavorite = favoriteIds.contains(hosting.id),
                size = 25.0
            )
        }

        searchResults = results.sortedBy { it.distance }
    }





//    fun saveNewHosting(hosting: Hosting) {
//        viewModelScope.launch {
//            val coords = if (hosting.location.isNotBlank()) {
//                getCoordinatesFromAddress(getApplication(), hosting.location)
//            } else {
//                null
//            }
//
//            val hostingCompleto = hosting.copy(
//                lat = coords?.latitude,
//                lng = coords?.longitude
//            )
//
//            db.saveHosting(hostingCompleto) {
//            }
//        }
//    }



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

fun getMockHostingsList(): List<Hosting> {
    // Lista de coordenadas reais em Recife para teste
    val recifeLocations = listOf(
        // 1. Boa Viagem (Perto da praia)
        LatLng(-8.110298, -34.894762),
        // 2. Parque da Jaqueira (Zona Norte)
        LatLng(-8.037502, -34.906323),
        // 3. Derby (Centro)
        LatLng(-8.057740, -34.896799),
        // 4. Casa Forte (Perto do Plaza Shopping)
        LatLng(-8.031910, -34.914560),
        // 5. Recife Antigo (Marco Zero)
        LatLng(-8.063169, -34.871139)
    )

    return List(5) { i ->
        val location = recifeLocations[i % recifeLocations.size]

        Hosting(
            id = i.toString(),
            name = "Creche Patinhas $i",
            type = if (i % 2 == 0) HostingType.COMPARTILHADO else HostingType.INDIVIDUAL,
            dailyRate = BigDecimal(145.00 + i),
            vacancies = 15 - i,
            address = "",
            location = location,
            complement = when(i) {
                0 -> "Boa Viagem - Próximo à Pracinha"
                1 -> "Jaqueira - Em frente ao Parque"
                2 -> "Derby - Ao lado da Praça"
                3 -> "Casa Forte - Perto do Shopping"
                else -> "Recife Antigo - Marco Zero"
            },

            services = listOf(
                Service("Alimentação"),
                Service("Ar-Condicionado"),
                Service("Passeio Diário"),
                Service("Banho")
            ),
            description = "Hospedagem localizada em área nobre de Recife. " +
                    "Ambiente climatizado e monitorado 24 horas para o conforto do seu pet.",
        )
    }
}
