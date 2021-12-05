package com.airmineral.agendakeun.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.airmineral.agendakeun.data.FirebaseInstance
import com.airmineral.agendakeun.data.model.Event
import com.airmineral.agendakeun.data.model.Group
import com.airmineral.agendakeun.data.model.User
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.tasks.await

class GroupRepository(private val firebaseInstance: FirebaseInstance) {
    companion object {
        const val TAG = "Group Repo"
    }

    fun saveGroup(groupData: Group, userList: List<String>): Boolean {
        return try {
            firebaseInstance.groupColRef.add(groupData)
                .addOnSuccessListener { doc ->
                    userList.forEach { user ->
                        firebaseInstance.userColRef.document(user)
                            .update("groupList", FieldValue.arrayUnion(doc.id))
                    }
                }
            true
        } catch (e: FirebaseFirestoreException) {
            Log.d(TAG + "_Save Group", e.message!!)
            false
        }
    }

    fun editGroup(
        groupData: Group,
        mapUserList: Map<String, Boolean>,
        deletedUser: MutableList<String>?,
        addedUser: MutableList<String>?
    ): Boolean {
        return try {
            firebaseInstance.groupColRef.document(groupData.groupId!!)
                .update("userList", mapUserList)

            deletedUser?.forEach {
                firebaseInstance.userColRef.document(it)
                    .update("groupList", FieldValue.arrayRemove(groupData.groupId))
            }
            addedUser?.forEach {
                firebaseInstance.userColRef.document(it)
                    .update("groupList", FieldValue.arrayUnion(groupData.groupId))
            }
            true
        } catch (e: FirebaseFirestoreException) {
            Log.d(TAG + "_Edit Group", e.message!!)
            false
        }
    }

    fun deleteGroup(group: Group): Boolean {
        return try {
            firebaseInstance.groupEventColRef(group.groupId!!).get()
                .addOnSuccessListener {
                    it.toObjects<Event>().forEach { e ->
                        firebaseInstance.groupEventColRef(group.groupId!!).document(e.eventId!!)
                            .delete()
                    }
                }
            firebaseInstance.groupColRef.document(group.groupId!!).delete()
            group.userList?.forEach {
                firebaseInstance.userColRef.document(it.key)
                    .update("groupList", FieldValue.arrayRemove(group.groupId))
            }
            true
        } catch (e: FirebaseFirestoreException) {
            Log.d(TAG + "_Delete Group", e.message!!)
            false
        }
    }

    suspend fun getAllGroups(): LiveData<List<Group>>? {
        return try {
            val currentUserID = firebaseInstance.auth.currentUser?.uid
            val field = "userList.$currentUserID"
            val res = MutableLiveData<List<Group>>()
            val groupList = mutableListOf<Group>()
            firebaseInstance.groupColRef.whereEqualTo(field, true)
                .get().await().forEach {
                    groupList.add(it.toObject())
                }
            res.postValue(groupList)
            res
        } catch (e: FirebaseFirestoreException) {
            Log.d(TAG + "_getAllG Catch", e.message!!)
            null
        }
    }

    suspend fun getGroupUserList(groupId: String, noCUser: Boolean): LiveData<List<User>>? {
        return try {
            val currentUserID = firebaseInstance.auth.currentUser?.uid
            val res = MutableLiveData<List<User>>()
            val userList = mutableListOf<User>()
            val groupData =
                firebaseInstance.groupColRef.document(groupId).get().await().toObject<Group>()
            groupData?.userList?.forEach {
                if (noCUser) {
                    if (it.key != currentUserID && it.key != groupData.creator) {
                        firebaseInstance.userColRef.document(it.key).get().await().toObject<User>()
                            .let { user ->
                                userList.add(user!!)
                            }
                    }
                } else {
                    firebaseInstance.userColRef.document(it.key).get().await().toObject<User>()
                        .let { user ->
                            userList.add(user!!)
                        }
                }
            }
            res.postValue(userList)
            res
        } catch (e: FirebaseFirestoreException) {
            Log.d(TAG + "_getGUserList catch", e.message!!)
            null
        }
    }
}