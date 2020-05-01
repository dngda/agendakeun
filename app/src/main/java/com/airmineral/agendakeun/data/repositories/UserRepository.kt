package com.airmineral.agendakeun.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.airmineral.agendakeun.data.model.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class UserRepository {
    companion object {
        const val TAG = "USER REPO"
    }

    private var db = Firebase.firestore
    private var auth = Firebase.auth
    private var userRef = db.collection("users")

    suspend fun saveUser(user: LiveData<User>) {
        try {
            userRef.document(getCurrentUser()!!.uid).set(user.value!!).await()
        } catch (e: FirebaseFirestoreException) {
            Log.d(TAG, e.message!!)
        }
    }

    fun getUserData(): LiveData<User> {
        val res = MutableLiveData<User>()
        userRef.document(getCurrentUser()!!.uid).get()
            .addOnSuccessListener {
                if (it != null) {
                    res.postValue(it.toObject<User>())
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
        return res
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun signOut() {
        auth.signOut()
    }
}