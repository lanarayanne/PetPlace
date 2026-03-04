package com.petplace.db.fb

import android.location.Location
import android.util.Log
import androidx.compose.runtime.snapshotFlow
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.type.LatLng
import com.petplace.model.Age
import com.petplace.model.Animal
import com.petplace.model.Booking
import com.petplace.model.Color
import com.petplace.model.Hosting
import com.petplace.model.Pet


class FBDatabase {
    interface Listener {
        fun onUserLoaded(user: FBUser)
        fun onUserSignOut()
    }
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    private var listener : Listener? = null


    init {
        auth.addAuthStateListener { authState ->
            if (authState.currentUser == null) {
                listener?.onUserSignOut()
                return@addAuthStateListener
            }
            val uid = authState.currentUser!!.uid
            val refCurrUser = db.collection("users").document(uid)


            refCurrUser.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("DEBUG_USER", "Erro ao acessar Firestore: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    try {
                        val user= snapshot.toObject(FBUser::class.java)
                        if (user != null) {
                            user.id = uid
                            Log.d("DEBUG_USER", "Usuário carregado com sucesso!")
                            listener?.onUserLoaded(user)
                        } else {
                            Log.e("DEBUG_USER", "O documento do usuário existe, mas retornou null")
                        }

                    } catch (e: Exception) {
                        Log.e("DEBUG_USER", "ERRO FATAL AO LER USUÁRIO: ${e.message}", e)
                    }
                } else {
                    Log.e("DEBUG_USER", "Documento do usuário NÃO EXISTE no banco!")
                }
            }
//            get().addOnSuccessListener {
//                it.toObject(FBUser::class.java)?.let { user ->
//                    listener?.onUserLoaded(user)
//                }
//            }

        }
    }


    fun startHostBookingsListener(userId: String, onUpdate: (List<Booking>) -> Unit) {
        db.collection("bookings")
            .whereEqualTo("host.id", userId)
            .addSnapshotListener { snapshots, error ->
                if (error != null) return@addSnapshotListener

                if (snapshots != null) {
                    val bookingsList = snapshots.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(FBBooking::class.java)?.toBooking()
                        } catch (e: Exception) {
                            null
                        }
                    }
                    onUpdate(bookingsList)
                }
            }
    }

    fun setListener(listener: Listener? = null) {
        this.listener = listener
    }

    fun register (user: FBUser) {
        if (auth.currentUser == null)
            throw RuntimeException("User not logged in!")
        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid + "").set(user);
    }

    companion object {
        fun updateProfile(
            name: String,
            phone: String,
            cep: String,
            address: String,
            lat: Double?,
            lng: Double?,
            onSuccess: () -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            val uid = Firebase.auth.currentUser?.uid

            if (uid != null) {
                val updates = mapOf(
                    "name" to name,
                    "phone" to phone,
                    "cep" to cep,
                    "address" to address,
                    "lat" to lat,
                    "lng" to lng,
                )

                Firebase.firestore
                    .collection("users")
                    .document(uid)
                    .update(updates)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure(it) }
            }
        }

        fun updatePetProfile(
            petId: Int,
            name: String,
            animal: String?,
            age: Age?,
            weight: Double,
            breed: String,
            color: String?,
            observations: String,
            picture: String?,
            onSuccess: () -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            val uid = Firebase.auth.currentUser?.uid
            if (uid == null) {
                onFailure(Exception("Usuário não logado."))
                return
            }

            val petRef = Firebase.firestore
                .collection("users")
                .document(uid)
                .collection("pets")
                .document(petId.toString())

            val updates = hashMapOf<String, Any?>(
                "name" to name,
                "weight" to weight,
                "breed" to breed,
                "observations" to observations,
                "picture" to picture
            )

            if (animal != null) updates["animal"] = animal
            if (age != null) updates["age"] = age
            if (color != null) updates["color"] = color

            petRef.update(updates)
                .addOnSuccessListener {
                    Log.d("DEBUG_FB", "Pet $petId atualizado com sucesso!")
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    Log.e("DEBUG_FB", "Erro ao atualizar pet $petId", e)
                    onFailure(e)
                }
        }

        fun deletePet(
            petId: Int,
            onSuccess: () -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            val uid = Firebase.auth.currentUser?.uid
            if (uid == null) {
                onFailure(Exception("Usuário não logado."))
                return
            }

            Firebase.firestore
                .collection("users")
                .document(uid)
                .collection("pets")
                .document(petId.toString())
                .delete()
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    onFailure(e)
                }
        }

        fun deleteProfile(
            onSuccess: () -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            val user = Firebase.auth.currentUser
            val uid = user?.uid

            if (uid == null || user == null) {
                onFailure(Exception("Usuário não logado."))
                return
            }

            val db = Firebase.firestore
            val userRef = db.collection("users").document(uid)
            val petsRef = userRef.collection("pets")

            petsRef.get()
                .addOnSuccessListener { snapshot ->
                    val batch = db.batch()
                    for (document in snapshot.documents) {
                        batch.delete(document.reference)
                    }
                    batch.delete(userRef)
                    batch.commit()
                        .addOnSuccessListener {
                            user.delete()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        onSuccess()
                                    } else {
                                        onFailure(task.exception ?: Exception("Erro ao excluir conta Auth"))
                                    }
                                }
                        }
                        .addOnFailureListener {e ->
                            onFailure(e)}
                }
                .addOnFailureListener { e ->
                    onFailure(e)
                }
        }

    }



    fun savePet(userId: String, pet: Pet, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val firebasePet = pet.toFBPet()

        db.collection("users")
            .document(userId)
            .collection("pets")
            .document(pet.id.toString())
            .set(firebasePet)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun startPetsListener(userId: String, onPetsUpdate: (List<Pet>) -> Unit) {
        db.collection("users")
            .document(userId)
            .collection("pets")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val pets = snapshot.toObjects(FBPet::class.java).map { it.toPet() }
                    onPetsUpdate(pets)
                }
            }
    }

    fun saveBooking(booking: Booking, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {

        try {
            val firebaseBooking = booking.toFBBooking()

            db.collection("bookings")
                .document(firebaseBooking.id ?: "")
                .set(firebaseBooking)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    onFailure(e)
                }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    fun startBookingsListener(userId: String, onUpdate: (List<Booking>) -> Unit) {
        Log.e("TESTE_RESERVA", "Procurando reservas onde o client.id seja EXATAMENTE: [$userId]")
        Log.d("DEBUG_BOOKING", "Buscando reservas para client.id = $userId")
        db.collection("bookings")
            .whereEqualTo("client.id", userId)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("DEBUG_BOOKING", "Erro de conexão com o Firestore", error)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    Log.d("DEBUG_BOOKING", "Encontrados ${snapshots.size()} documentos brutos.")

                    val bookingsList = snapshots.documents.mapNotNull { doc ->
                        try {
                            val fbBooking = doc.toObject(FBBooking::class.java)
                            fbBooking?.toBooking()
                        } catch (e: Exception) {
                            Log.e("DEBUG_BOOKING", "FALHA ao converter documento: ${doc.id}", e)
                            null
                        }
                    }
                    Log.d("DEBUG_BOOKING", "Lista final processada tem ${bookingsList.size} reservas.")
                    onUpdate(bookingsList)
                } else {
                    Log.d("DEBUG_BOOKING", "Snapshot veio nulo.")
                }
            }
    }


    fun saveHosting(hosting: Hosting, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val fbHosting = hosting.toFBHosting()

        db.collection("hostings")
            .document(hosting.id.toString())
            .set(fbHosting)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { onFailure(it) }
    }


    fun getUserHostings(userId: String, onResult: (List<Hosting>) -> Unit) {
        db.collection("hostings")
            .whereEqualTo("owner.id", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val hostings = snapshot.toObjects(FBHosting::class.java).map { it.toHosting() }
                    onResult(hostings)
                }
            }
    }

    fun getAllHostings(onResult: (List<Hosting>) -> Unit) {
        db.collection("hostings")
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null) {
                    val list = snapshot.toObjects(FBHosting::class.java).map { it.toHosting() }
                    onResult(list)
                }
            }
    }

    fun getAllBookings(onResult: (List<Booking>) -> Unit) {
        db.collection("bookings")
            .get()
            .addOnSuccessListener { documentos ->
                val lista = documentos.mapNotNull { doc ->
                    doc.toObject(FBBooking::class.java).toBooking()
                }
                onResult(lista)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }


    fun toggleFavorite(
        userId: String,
        hostingId: String,
        isFavorite: Boolean
    ) {
        val ref = db
            .collection("users")
            .document(userId)
            .collection("favorites")
            .document(hostingId)

        if (isFavorite) {
            ref.set(mapOf("createdAt" to System.currentTimeMillis()))
        } else {
            ref.delete()
        }
    }

    fun startFavoritesListener(
        userId: String,
        onResult: (List<String>) -> Unit
    ) {
        db
            .collection("users")
            .document(userId)
            .collection("favorites")
            .addSnapshotListener { snapshot, _ ->

                val ids = snapshot?.documents?.map { it.id } ?: emptyList()
                onResult(ids)
            }
    }


}