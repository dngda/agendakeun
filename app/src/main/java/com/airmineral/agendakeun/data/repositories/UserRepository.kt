package com.airmineral.agendakeun.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.airmineral.agendakeun.data.FirebaseInstance
import com.airmineral.agendakeun.data.model.User
import com.airmineral.agendakeun.data.preferences.PreferenceProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firebaseInstance: FirebaseInstance,
    private val preferenceProvider: PreferenceProvider
) {
    companion object {
        const val TAG = "User Repo"
    }

    suspend fun saveUser(user: LiveData<User>) {
        try {
            firebaseInstance.userColRef.document(getCurrentUser().uid).set(user.value!!).await()
        } catch (e: FirebaseFirestoreException) {
            Log.d(TAG, e.message!!)
        }
    }

    fun getUserData(): LiveData<User> {
        val res = MutableLiveData<User>()
        val docsRef = firebaseInstance.userColRef.document(getCurrentUser().uid)
        docsRef.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if (documentSnapshot != null && documentSnapshot.exists()) {
                res.postValue(documentSnapshot.toObject<User>())
            }
            if (firebaseFirestoreException != null) {
                Log.w(TAG, "get failed with ", firebaseFirestoreException)
                return@addSnapshotListener
            }
        }
        return res
    }

    fun getSpecificUserData(uid: String): LiveData<User> {
        val res = MutableLiveData<User>()
        val docsRef = firebaseInstance.userColRef.document(uid)
        docsRef.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if (documentSnapshot != null && documentSnapshot.exists()) {
                res.postValue(documentSnapshot.toObject<User>())
            }
            if (firebaseFirestoreException != null) {
                Log.w(TAG, "get failed with ", firebaseFirestoreException)
                return@addSnapshotListener
            }
        }
        return res
    }

    fun getCurrentUser(): FirebaseUser {
        return firebaseInstance.auth.currentUser!!
    }

    fun signOut() {
        firebaseInstance.auth.signOut()
    }

    suspend fun getAllUserList(): LiveData<List<User>>? {
        return try {
            val results = MutableLiveData<List<User>>()
            val userList = mutableListOf<User>()
            val orgCode = preferenceProvider.getStrings("orgCode")
            firebaseInstance.userColRef.whereEqualTo("orgCode", orgCode).get().await().forEach {
                if (it.id != getCurrentUser().uid)
                    userList.add(it.toObject())
            }
            results.postValue(userList)
            results
        } catch (e: Exception) {
            Log.d(TAG, e.message!!)
            null
        }
    }

    suspend fun getAllUserExcMember(groupId: String?): LiveData<List<User>>? {
        return try {
            val results = MutableLiveData<List<User>>()
            val userList = mutableListOf<User>()
            val orgCode = preferenceProvider.getStrings("orgCode")

            firebaseInstance.userColRef.whereEqualTo("orgCode", orgCode)
                .get().await().forEach {
                    if (it.toObject<User>().groupList.isNullOrEmpty()) {
                        userList.add(it.toObject())
                    } else {
                        var isMember = false
                        it.toObject<User>().groupList?.forEach { id ->
                            if (id == groupId)
                                isMember = true
                        }
                        if (!isMember) userList.add(it.toObject())
                    }
                }
            results.postValue(userList)
            results
        } catch (e: Exception) {
            Log.d(TAG, e.message!!)
            null
        }
    }
}