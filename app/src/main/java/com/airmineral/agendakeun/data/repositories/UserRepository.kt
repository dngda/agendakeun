package com.airmineral.agendakeun.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.airmineral.agendakeun.data.model.Group
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
    private var groupRef = db.collection("groups").document()

    suspend fun saveUser(user: LiveData<User>): Boolean {
        return try {
            userRef.document(getCurrentUser()!!.uid).set(user.value!!).await()
            true
        } catch (e: FirebaseFirestoreException) {
            Log.d(TAG, e.message!!)
            false
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

    suspend fun getAllUserList(): LiveData<List<User>>? {
        return try {
            val res = MutableLiveData<List<User>>()
            val userList = mutableListOf<User>()
            userRef.get().await().forEach {
                userList.add(it.toObject())
            }
            res.postValue(userList)
            res
        } catch (e: Exception) {
            Log.d(TAG, e.message!!)
            null
        }
    }

    suspend fun saveGroup(groupData: Group): Boolean {
        return try {
            groupRef.set(groupData).await()
            true
        } catch (e: FirebaseFirestoreException) {
            Log.d(TAG, e.message!!)
            false
        }
    }
}