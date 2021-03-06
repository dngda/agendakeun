package com.airmineral.agendakeun.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.airmineral.agendakeun.data.FirebaseInstance.auth
import com.airmineral.agendakeun.data.FirebaseInstance.groupColRef
import com.airmineral.agendakeun.data.FirebaseInstance.userColRef
import com.airmineral.agendakeun.data.model.Group
import com.airmineral.agendakeun.data.model.User
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class GroupRepository {
    companion object {
        const val TAG = "Group Repo"
    }

    suspend fun saveGroup(groupData: Group): Boolean {
        return try {
            groupColRef.document().set(groupData).await()
            true
        } catch (e: FirebaseFirestoreException) {
            Log.d(UserRepository.TAG, e.message!!)
            false
        }
    }

    suspend fun getAllGroups(): LiveData<List<Group>>? {
        return try {
            val currentUserID = auth.currentUser?.uid
            val field = "userList.$currentUserID"
            val res = MutableLiveData<List<Group>>()
            val groupList = mutableListOf<Group>()
            groupColRef.whereEqualTo(field, true)
                .get().await().forEach {
                    groupList.add(it.toObject())
                }
            res.postValue(groupList)
            Log.d(TAG, res.value.toString())
            res
        } catch (e: FirebaseFirestoreException) {
            Log.d(TAG, e.message!!)
            null
        }
    }

    suspend fun getGroupUserList(groupId: String): LiveData<List<User>>? {
        return try {
            val res = MutableLiveData<List<User>>()
            val userList = mutableListOf<User>()
            val groupData = groupColRef.document(groupId).get().await().toObject<Group>()
            groupData?.userList?.forEach {
                userColRef.document(it.key).get().await().toObject<User>().let { user ->
                    userList.add(user!!)
                }
            }
            res.postValue(userList)
            Log.d(TAG, res.value.toString())
            res
        } catch (e: FirebaseFirestoreException) {
            Log.d(TAG, e.message!!)
            null
        }
    }
}