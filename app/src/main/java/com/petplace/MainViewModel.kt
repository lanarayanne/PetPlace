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
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.Firebase
import com.petplace.api.RetrofitClient
import com.petplace.model.SearchCriteria
import com.petplace.ui.formatDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException


class MainViewModel (private val db: FBDatabase) : ViewModel(), FBDatabase.Listener {
    private val _allHostings = mutableStateListOf<Hosting>()
    val allHostings: List<Hosting>
        get() = _allHostings
    private val _allBookings = mutableStateListOf<Booking>()
    val allBookings: List<Booking>
        get() = _allBookings
    private val _userHostings = mutableStateListOf<Hosting>()
    val userHostings: List<Hosting>
        get() = _userHostings
    private val _user = mutableStateOf<User?> (null)
    val user : User?
        get() = _user.value
    private val _bookings = mutableStateListOf<Booking>()
    val booking: List<Booking>
        get() = _bookings

    private val _hostBookings = mutableStateListOf<Booking>()
    val hostBookings: List<Booking>
        get() = _hostBookings

    var userCepState by mutableStateOf("")
    var hostingCepState by mutableStateOf("")
    var hostingAddressState by mutableStateOf("")
    var hostingLatState by mutableStateOf<Double?>(null)
    var hostingLngState by mutableStateOf<Double?>(null)
    var hostingCepError by mutableStateOf<String?>(null)

    var userCepError by mutableStateOf<String?>(null)

    private val _pets = mutableStateListOf<Pet>()
    val pets: List<Pet>
        get() = _pets

    val hostingPreviews = mutableStateListOf<PlacePreview>()

    var selectedHosting by mutableStateOf<Hosting?>(null)
        private set

    var selectedPet by mutableStateOf<Pet?>(null)
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

    var isHostMode by mutableStateOf(false)
        private set

    var userAddressState by mutableStateOf("")
    var userLatState by mutableStateOf<Double?>(null)
    var userLngState by mutableStateOf<Double?>(null)
    var isLoadingCep by mutableStateOf(false)

    fun toggleAppMode() {
        isHostMode = !isHostMode

    }

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

    fun loadUserData(user: User) {
        userCepState = user.cep ?: ""
        userAddressState = user.address ?: ""

    }

    fun loadHostingData(hosting: Hosting) {
        hostingCepState = hosting.cep ?: ""
        hostingAddressState = hosting.address ?: ""

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

    fun clearHostingForm() {
        hostingCepState = ""
        hostingAddressState = ""
        hostingLatState = null
        hostingLngState = null
        hostingCepError = null
    }

    fun clearUserForm() {
        userCepState = ""
        userAddressState = ""
        userLatState = null
        userLngState = null
        userCepError = null
    }

    fun fetchHostingAddress(context: Context, cep: String) {
        searchCep(context, cep,
            onSuccess = { address, lat, lng ->
                hostingAddressState = address
                hostingLatState = lat
                hostingLngState = lng
                hostingCepError = null
            },
            onError = { msg ->
                hostingCepError = msg
                hostingAddressState = ""
            }
        )
    }



    fun fetchUserAddress(context: Context, cep: String) {
        searchCep(context, cep,
            onSuccess = { address, lat, lng ->
                userAddressState = address
                userLatState = lat
                userLngState = lng
                userCepError = null
            },
            onError = { msg ->
                userCepError = msg
                userAddressState = ""
            }
        )
    }

    private fun searchCep(
        context: Context,
        cep: String,
        onSuccess: (String, Double?, Double?) -> Unit,
        onError: (String) -> Unit
    ) {
        val cleanCep = cep.replace(Regex("[^0-9]"), "")
        if (cleanCep.length != 8) return

        isLoadingCep = true

        viewModelScope.launch {
            try {
                val response = RetrofitClient.brasilApi.getAddressByCep(cleanCep)
                val fullAddress = "${response.street}, ${response.neighborhood} - ${response.city}/${response.state}"

                var lat = response.location?.coordinates?.latitude?.toDoubleOrNull()
                var lng = response.location?.coordinates?.longitude?.toDoubleOrNull()

                if (lat == null || lng == null) {
                    val fallback = getCoordinatesFromAddress(context, fullAddress)
                    lat = fallback?.latitude
                    lng = fallback?.longitude
                }

                onSuccess(fullAddress, lat, lng)

            } catch (e: Exception) {
                onError("CEP inválido ou erro de conexão.")
            } finally {
                isLoadingCep = false
            }
        }
    }

    init {
        db.setListener(this)
        getAllHostings()
        getAllBookings()
    }

    private fun getAllBookings() {
        db.getAllBookings { listaDoBanco ->
            _allBookings.clear()
            _allBookings.addAll(listaDoBanco)

            performSearch()
        }
    }

    private fun getAllHostings() {
        db.getAllHostings { listaDoBanco ->
            _allHostings.clear()
            _allHostings.addAll(listaDoBanco)

            performSearch()
        }
    }

    fun getHostingsByUser() {
        val uid = user?.id ?: return
        db.getUserHostings(uid) { listaDoBanco ->
            _userHostings.clear()
            _userHostings.addAll(listaDoBanco)
        }
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
            performSearch()
        }

        db.startPetsListener(userId) { petsDoBanco ->
            val currentUser = _user.value
            if (currentUser != null) {
                _user.value = currentUser.copy(pets = petsDoBanco)
            }
        }

        db.startBookingsListener(userId) { reservasDoBanco ->
            _bookings.clear()
            _bookings.addAll(reservasDoBanco)
            atualizarStatusAutomaticamente(reservasDoBanco)
        }

        db.startHostBookingsListener(userId) { reservasRecebidas ->
            _hostBookings.clear()
            _hostBookings.addAll(reservasRecebidas)
            atualizarStatusAutomaticamente(reservasRecebidas)
        }


    }



    override fun onUserSignOut() {
        //TODO("Not yet implemented")
    }

    fun selectHostingById(id: String) {
        selectedHosting = _allHostings.find { it.id == id }
    }



//    fun selectPetById(id: String) {
//        selectedPet = _pets.find { it.id == id }
//    }

    fun saveNewHosting(
        context: Context,
        name: String,
        type: HostingType,
        dailyRate: Double,
        vacancies: Int,
        size: Double,
        cep: String,
        address: String,
        lat: Double?,
        lng: Double?,
        description: String,
        imageUri: Uri?,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val currentUser = user ?: return

        fun salvarNoBanco(pictureUrl: String?) {
            viewModelScope.launch {
                val locationObj = if (lat != null && lng != null) {
                    LatLng(lat, lng)
                } else {
                    getCoordinatesFromAddress(context, address)
                }

                val listaDeFotos = if (pictureUrl != null) listOf(pictureUrl) else null

                val newHosting = Hosting(
                    id = java.util.UUID.randomUUID().toString(),
                    name = name,
                    type = type,
                    dailyRate = BigDecimal(dailyRate),
                    vacancies = vacancies,
                    size = size,
                    cep = cep,
                    address = address,
                    location = locationObj,
                    description = description,
                    pictures = listaDeFotos,
                    owner = currentUser
                )

                db.saveHosting(newHosting, onSuccess, onFailure)
            }
        }

        if (imageUri != null) {
            uploadImageToCloudinary(
                imageUri = imageUri,
                onSuccess = { secureUrl ->
                    salvarNoBanco(secureUrl)
                },
                onError = { errorMsg ->
                    onFailure(Exception("Erro no upload da imagem: $errorMsg"))
                }
            )
        } else {
            salvarNoBanco(null)
        }
    }


    fun saveNewPet(
        name: String,
        animal: String,
        breed: String?,
        age: Age,
        weight: Double,
        birthYear: Int?,
        color: String?,
        observations: String?,
        imageUri: Uri?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val currentUser = _user.value ?: return

        fun salvarNoBanco(pictureUrl: String?) {
            val newPet = Pet(
                id = (System.currentTimeMillis() % 100000).toInt(),
                name = name,
                animal = animal,
                age = age,
                weight = weight,
                birthYear = birthYear,
                breed = breed,
                color = color,
                observations = observations,
                picture = pictureUrl
            )


        db.savePet(
            userId = currentUser.id,
            pet = newPet,
            onSuccess = {
                val updatedPets = currentUser.pets?.toMutableList() ?: mutableListOf()
                updatedPets.add(newPet)
                _user.value = currentUser.copy(pets = updatedPets)
                onSuccess()
            },
            onFailure = { e ->
                e.printStackTrace()
            }
        )
    }
        if (imageUri != null) {
            uploadImageToCloudinary(
                imageUri = imageUri,
                onSuccess = { secureUrl ->
                    salvarNoBanco(secureUrl)
                },
                onError = { errorMsg ->
                    onError("Erro no upload da imagem: $errorMsg")
                }
            )
        } else {
            salvarNoBanco(null)
        }
    }

    val calculatedDays: Int
        get() {
            val start = currentSearch.startDate?.time ?: 0L
            val end = currentSearch.endDate?.time ?: 0L

            return if (start > 0L && end > start) {
                ((end - start) / (1000 * 60 * 60 * 24)).toInt()
            } else if (currentSearch.dailyRates > 0) {
                currentSearch.dailyRates
            } else {
                1
            }
        }

    val bookingTotalValue: BigDecimal
        get() {
            val hosting = selectedHosting ?: return BigDecimal.ZERO
            return hosting.dailyRate.multiply(BigDecimal(calculatedDays))
        }

    fun cancelBooking(booking: Booking, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val canceledBooking = booking.copy(status = Status.CANCELADA)

        db.saveBooking(
            booking = canceledBooking,
            onSuccess = { onSuccess() },
            onFailure = { e -> onError(e.message ?: "Erro ao cancelar reserva") }
        )
    }

    private fun atualizarStatusAutomaticamente(listaDeReservas: List<Booking>) {
        val hoje = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.time

        val formato = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale("pt", "BR"))

        for (reserva in listaDeReservas) {
            if (reserva.status == Status.CANCELADA) continue

            try {
                val dataCheckIn = formato.parse(reserva.checkIn)
                val dataCheckOut = formato.parse(reserva.checkOut)

                if (dataCheckIn != null && dataCheckOut != null) {
                    var novoStatus = reserva.status

                    if (hoje.after(dataCheckOut)) {
                        novoStatus = Status.CONCLUIDA
                    }
                    else if (!hoje.before(dataCheckIn) && !hoje.after(dataCheckOut)) {
                        novoStatus = Status.EMANDAMENTO
                    }

                    if (novoStatus != reserva.status) {
                        val reservaAtualizada = reserva.copy(status = novoStatus)
                        db.saveBooking(
                            booking = reservaAtualizada,
                            onSuccess = { Log.d("DEBUG_STATUS", "Status da reserva ${reserva.id} atualizado para $novoStatus automaticamente!") },
                            onFailure = { Log.e("DEBUG_STATUS", "Falha ao atualizar status automaticamente") }
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("DEBUG_STATUS", "Erro ao converter datas para atualização: ${e.message}")
            }
        }
    }

    fun saveBooking(
        selectedPets: List<Pet>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit) {
        val hosting = selectedHosting
        val currentUser = user

        if (hosting == null || currentUser == null) {
            onError("Erro: Falta usuário ou hospedagem.")
            return
        }

        val bookingId = java.util.UUID.randomUUID().toString()

        val newBooking = Booking(
            id = bookingId,
            host = hosting.owner!!,
            client = currentUser,
            pets = selectedPets,
            hosting = hosting,
            value = bookingTotalValue,
            days = calculatedDays,
            checkIn = formatDate(currentSearch.startDate),
            checkOut = formatDate(currentSearch.endDate),
            status = Status.PROXIMA
        )

        db.saveBooking(
            booking = newBooking,
            onSuccess = {
                getAllBookings()
                onSuccess()},
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
        Log.d("DEBUG_SEARCH", "Total de hospedagens carregadas: ${_allHostings.size}")

        val results = _allHostings.mapNotNull { hosting ->
            val temVaga = isHostingAvailable(
                hosting = hosting,
                searchStart = searchCriteria.startDate,
                searchEnd = searchCriteria.endDate,
                requestedPets = userPetsCount,
                allBookings = _allBookings
            )

            if (!temVaga) {
                return@mapNotNull null
            }

            val distanceKm =
                if (myLocation != null)
                    calculateDistanceKm(myLocation, hosting.location)
                else
                    0.0

//            if (myLocation != null && distanceKm > 30) return@mapNotNull null //TODO:
//            if (hosting.vacancies < userPetsCount) return@mapNotNull null //TODO: descomentar, esse filtro etá funcionando

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
                picture = hosting.pictures?.firstOrNull(),
                isFavorite = favoriteIds.contains(hosting.id.toString()),
                size = hosting.size
            )
        }

        searchResults = results.sortedBy { it.distance }
        Log.d("DEBUG_SEARCH", "Resultados após filtro: ${results.size}")
        searchResults = results.sortedBy { it.distance }
    }

    fun updateHosting(
        hostingId: String,
        name: String,
        type: HostingType,
        dailyRate: Double,
        vacancies: Int,
        size: Double,
        cep: String,
        address: String,
        lat: Double?,
        lng: Double?,
        description: String,
        context: Context,
        imageUri: Uri?,
        existingPictureUrl: String?,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val currentUser = user ?: return

        fun salvarNoBanco(pictureUrl: String?) {
            viewModelScope.launch {
                val locationObj = if (lat != null && lng != null) {
                    LatLng(lat, lng)
                } else {
                    getCoordinatesFromAddress(context, address)
                }

                val listaDeFotos = if (pictureUrl != null) listOf(pictureUrl) else null

                val updatedHosting = Hosting(
                    id = hostingId,
                    name = name,
                    type = type,
                    dailyRate = BigDecimal(dailyRate),
                    vacancies = vacancies,
                    size = size,
                    cep = cep,
                    address = address,
                    location = locationObj,
                    description = description,
                    pictures = listaDeFotos,
                    owner = currentUser
                )
                db.saveHosting(updatedHosting, onSuccess, onFailure)
            }
        }

        if (imageUri != null) {
            uploadImageToCloudinary(
                imageUri = imageUri,
                onSuccess = { secureUrl ->
                    salvarNoBanco(secureUrl)
                },
                onError = { errorMsg ->
                    onFailure(Exception("Erro no upload da imagem: $errorMsg"))
                }
            )
        } else {
            salvarNoBanco(existingPictureUrl)
        }
    }

    fun updatePet(
        petId: Int,
        name: String,
        animal: String,
        breed: String,
        age: Age,
        weight: Double,
        color: String,
        observations: String,
        imageUri: Uri?,
        existingPictureUrl: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        fun salvarNoBanco(pictureUrl: String?) {
            FBDatabase.updatePetProfile(
                petId = petId,
                name = name,
                animal = animal,
                age = age,
                weight = weight,
                breed = breed,
                color = color,
                observations = observations,
                picture = pictureUrl,
                onSuccess = onSuccess,
                onFailure = { e -> onError(e.message ?: "Erro ao atualizar Pet") }
            )
        }

        if (imageUri != null) {
            uploadImageToCloudinary(
                imageUri = imageUri,
                onSuccess = { secureUrl -> salvarNoBanco(secureUrl) },
                onError = { errorMsg -> onError("Erro no upload da foto: $errorMsg") }
            )
        } else {
            salvarNoBanco(existingPictureUrl)
        }
    }

    private fun isHostingAvailable(
        hosting: Hosting,
        searchStart: java.util.Date?,
        searchEnd: java.util.Date?,
        requestedPets: Int,
        allBookings: List<Booking>
    ): Boolean {
        if (searchStart == null || searchEnd == null) {
            return hosting.vacancies >= requestedPets
        }

        val activeBookings = allBookings.filter { reserva ->
            reserva.hosting.id == hosting.id &&
                    (reserva.status == Status.PROXIMA || reserva.status == Status.EMANDAMENTO)
        }

        if (activeBookings.isEmpty()) {
            return hosting.vacancies >= requestedPets
        }

        val formato = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale("pt", "BR"))
        val calendar = java.util.Calendar.getInstance()
        calendar.time = searchStart

        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)

        val endCalendar = java.util.Calendar.getInstance()
        endCalendar.time = searchEnd
        endCalendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        endCalendar.set(java.util.Calendar.MINUTE, 0)
        endCalendar.set(java.util.Calendar.SECOND, 0)
        endCalendar.set(java.util.Calendar.MILLISECOND, 0)
        val safeEnd = endCalendar.time

        while (calendar.time.before(safeEnd)) {
            val diaAtual = calendar.time
            var petsNesteDia = 0

            for (reserva in activeBookings) {
                try {
                    val dataCheckIn = formato.parse(reserva.checkIn)
                    val dataCheckOut = formato.parse(reserva.checkOut)

                    if (dataCheckIn != null && dataCheckOut != null) {
                        if (!diaAtual.before(dataCheckIn) && diaAtual.before(dataCheckOut)) {
                            petsNesteDia += reserva.pets.size
                        }
                    }
                } catch (e: Exception) {
                    Log.e("DEBUG_DISPONIBILIDADE", "Erro ao ler datas da reserva ${reserva.id}")
                }
            }

            if ((petsNesteDia + requestedPets) > hosting.vacancies) {
                return false
            }

            calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
        }

        return true
    }

    val availableVacancies: Int
        get() {
            val hosting = selectedHosting ?: return 0
            val start = currentSearch.startDate
            val end = currentSearch.endDate

            if (start == null || end == null) return hosting.vacancies

            val activeBookings = _allBookings.filter { reserva ->
                reserva.hosting.id == hosting.id &&
                        (reserva.status == Status.PROXIMA || reserva.status == Status.EMANDAMENTO)
            }

            if (activeBookings.isEmpty()) return hosting.vacancies

            val formato = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale("pt", "BR"))

            val calendar = java.util.Calendar.getInstance()
            calendar.time = start
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
            calendar.set(java.util.Calendar.MINUTE, 0)
            calendar.set(java.util.Calendar.SECOND, 0)
            calendar.set(java.util.Calendar.MILLISECOND, 0)

            val endCalendar = java.util.Calendar.getInstance()
            endCalendar.time = end
            endCalendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
            endCalendar.set(java.util.Calendar.MINUTE, 0)
            endCalendar.set(java.util.Calendar.SECOND, 0)
            endCalendar.set(java.util.Calendar.MILLISECOND, 0)
            val safeEnd = endCalendar.time

            var maxPetsOccupied = 0

            while (calendar.time.before(safeEnd)) {
                val diaAtual = calendar.time
                var petsNesteDia = 0

                for (reserva in activeBookings) {
                    try {
                        val dataCheckIn = formato.parse(reserva.checkIn)
                        val dataCheckOut = formato.parse(reserva.checkOut)

                        if (dataCheckIn != null && dataCheckOut != null) {
                            if (!diaAtual.before(dataCheckIn) && diaAtual.before(dataCheckOut)) {
                                petsNesteDia += reserva.pets.size
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("DEBUG_VAGAS", "Erro ao ler datas")
                    }
                }

                if (petsNesteDia > maxPetsOccupied) {
                    maxPetsOccupied = petsNesteDia
                }

                calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
            }

            val vagasRestantes = hosting.vacancies - maxPetsOccupied

            return if (vagasRestantes > 0) vagasRestantes else 0
        }

    fun uploadImageToCloudinary(
        imageUri: Uri,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        MediaManager.get().upload(imageUri)
            .unsigned("hrdyfjan")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(requestId: String?, resultData: Map<*, *>) {
                    val secureUrl = resultData["secure_url"] as? String ?: ""
                    onSuccess(secureUrl)
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    onError("Erro ao fazer upload: ${error?.description}")
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
            }).dispatch()
    }

    fun sortByLowestPrice() {
        searchResults = searchResults.sortedBy { it.value }
    }

    fun sortByHighestPrice() {
        searchResults = searchResults.sortedByDescending { it.value }
    }

    fun sortByShortestDistance () {
        searchResults = searchResults.sortedBy { it.distance }
    }

    fun sortByGreatestDistance() {
        searchResults = searchResults.sortedByDescending { it.distance }
    }

    fun sortByShortestSize () {
        searchResults = searchResults.sortedBy { it.size }
    }

    fun sortByGreatestSize() {
        searchResults = searchResults.sortedByDescending { it.size }
    }

    fun sortByRatings () {
        searchResults = searchResults.sortedBy { it.rating }
    }

    fun sortByEvaluations() {
        searchResults = searchResults.sortedByDescending { it.evaluation }
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

