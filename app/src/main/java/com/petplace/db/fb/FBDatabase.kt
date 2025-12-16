package com.petplace.db.fb

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.petplace.model.Booking
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
        auth.addAuthStateListener { auth ->
            if (auth.currentUser == null) {
                listener?.onUserSignOut()
                return@addAuthStateListener
            }

            val refCurrUser = db.collection("users").document(auth.currentUser!!.uid)
            refCurrUser.get().addOnSuccessListener {
                it.toObject(FBUser::class.java)?.let { user ->
                    listener?.onUserLoaded(user)
                }
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
            address: String,
            onSuccess: () -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            val uid = Firebase.auth.currentUser?.uid

            if (uid != null) {
                val updates = mapOf(
                    "name" to name,
                    "phone" to phone,
                    "address" to address
                )

                Firebase.firestore // Use a instância estática direta
                    .collection("users")
                    .document(uid)
                    .update(updates)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure(it) }
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
        Log.d("DEBUG_FB", "FBDatabase: Tentando salvar reserva ${booking.id}")

        try {
            val firebaseBooking = booking.toFBBooking()

            db.collection("bookings")
                .document(firebaseBooking.id ?: "")
                .set(firebaseBooking)
                .addOnSuccessListener {
                    Log.d("DEBUG_FB", "FBDatabase: Reserva salva com SUCESSO!")
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    Log.e("DEBUG_FB", "FBDatabase: Falha ao salvar no Firestore", e)
                    onFailure(e)
                }
        } catch (e: Exception) {
            Log.e("DEBUG_FB", "FBDatabase: Erro de conversão antes de salvar", e)
            onFailure(e)
        }
    }

    fun startBookingsListener(userId: String, onUpdate: (List<com.petplace.model.Booking>) -> Unit) {
        Log.d("DEBUG_FB", "FBDatabase: Iniciando listener de reservas para client.id = $userId")

        db.collection("bookings")
            .whereEqualTo("client.id", userId)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("DEBUG_FB", "FBDatabase: Erro no Listener", error)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    Log.d("DEBUG_FB", "FBDatabase: Listener recebeu ${snapshots.size()} documentos.")

                    val bookingsList = snapshots.documents.mapNotNull { doc ->
                        try {
                            val fbBooking = doc.toObject(FBBooking::class.java)
                            fbBooking?.toBooking()
                        } catch (e: Exception) {
                            Log.e("DEBUG_FB", "FBDatabase: Erro ao converter documento ${doc.id}", e)
                            null
                        }
                    }
                    onUpdate(bookingsList)
                } else {
                    Log.d("DEBUG_FB", "FBDatabase: Snapshot veio nulo.")
                }
            }
    }
}