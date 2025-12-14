package com.petplace.db.fb

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.firestore

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

            val currentUser = auth.currentUser

            if (currentUser == null) {
                //citiesListReg?.remove()
                listener?.onUserSignOut()
                return@addAuthStateListener
            }

            val refCurrUser = db.collection("users").document(auth.currentUser!!.uid)
            refCurrUser.get().addOnSuccessListener {
                it.toObject(FBUser::class.java)?.let { user ->
                    listener?.onUserLoaded(user)
                }
            }

//            db.collection("users")
//                .document(currentUser.uid)
//                .get()
//                .addOnSuccessListener {
//                    it.toObject(FBUser::class.java)
//                        ?.let { user -> listener?.onUserLoaded(user) }
//                }



        }
    }

    fun setListener(listener: Listener? = null) {
        this.listener = listener
    }

    fun register(user: FBUser): com.google.android.gms.tasks.Task<Void> {
        if (auth.currentUser == null)
            throw RuntimeException("User not logged in!")
        val uid = auth.currentUser!!.uid

        return db.collection("users").document(uid).set(user)
    }

}