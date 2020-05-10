package com.airmineral.agendakeun.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.airmineral.agendakeun.data.model.User
import com.airmineral.agendakeun.data.repositories.FirebaseInstance.auth
import com.airmineral.agendakeun.data.repositories.FirebaseInstance.userColRef
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class UserRepository {
    companion object {
        const val TAG = "User Repo"
    }

    suspend fun saveUser(user: LiveData<User>) {
        try {
            userColRef.document(getCurrentUser()!!.uid).set(user.value!!).await()
        } catch (e: FirebaseFirestoreException) {
            Log.d(TAG, e.message!!)
        }
    }

    fun getUserData(): LiveData<User> {
        val res = MutableLiveData<User>()
        userColRef.document(getCurrentUser()!!.uid).get()
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
            userColRef.get().await().forEach {
                if (it.id != getCurrentUser()!!.uid)
                userList.add(it.toObject())
            }
            res.postValue(userList)
            Log.d(TAG, res.value.toString())
            res
        } catch (e: Exception) {
            Log.d(TAG, e.message!!)
            null
        }
    }
}