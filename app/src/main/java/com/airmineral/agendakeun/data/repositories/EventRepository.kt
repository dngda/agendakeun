package com.airmineral.agendakeun.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.airmineral.agendakeun.data.FirebaseInstance
import com.airmineral.agendakeun.data.model.Event
import com.airmineral.agendakeun.data.preferences.PreferenceProvider
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import java.util.*

class EventRepository(
    private val firebaseInstance: FirebaseInstance,
    private val preferenceProvider: PreferenceProvider
) {
    companion object {
        const val TAG = "Event Repo"
    }

    suspend fun saveGroupEvent(groupId: String, event: Event) {
        try {
            firebaseInstance.groupEventColRef(groupId).document().set(event).await()
        } catch (e: FirebaseFirestoreException) {
            Log.d(TAG, e.message!!)
        }
    }

    fun deleteGroupEvent(groupId: String, eventID: String) {
        firebaseInstance.groupEventColRef(groupId).document(eventID).delete()
    }

    suspend fun getCurEventList(): LiveData<List<Event>>? {
        return try {
            val curEventList = MutableLiveData<List<Event>>()
            val currentUserID = firebaseInstance.auth.currentUser?.uid
            val curDate = Calendar.getInstance().time
            val groupList: List<String> =
                preferenceProvider.getUserGroupList(currentUserID!!)
            val eventList = mutableListOf<Event>()
            groupList.forEach { groupId ->
                firebaseInstance.groupEventColRef(groupId)
                    .whereGreaterThanOrEqualTo("date", curDate)
                    .get().await().forEach { event ->
                        eventList.add(event.toObject())
                    }
            }
            eventList.sortBy { it.date }
            curEventList.postValue(eventList)
            curEventList
        } catch (e: FirebaseFirestoreException) {
            Log.d(TAG, e.message!!)
            null
        }
    }

    suspend fun getPastEventList(): LiveData<List<Event>>? {
        return try {
            val pastEventList = MutableLiveData<List<Event>>()
            val currentUserID = firebaseInstance.auth.currentUser?.uid
            val groupList: List<String> =
                preferenceProvider.getUserGroupList(currentUserID!!)
            val eventList = mutableListOf<Event>()
            groupList.forEach { groupId ->
                firebaseInstance.groupEventColRef(groupId)
                    .get().await().forEach { event ->
                        eventList.add(event.toObject())
                    }
            }
            eventList.sortByDescending { it.date }
            pastEventList.postValue(eventList)
            pastEventList
        } catch (e: FirebaseFirestoreException) {
            Log.d(TAG, e.message!!)
            null
        }
    }

}