package com.airmineral.agendakeun.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.airmineral.agendakeun.data.FirebaseInstance
import com.airmineral.agendakeun.data.FirebaseInstance.auth
import com.airmineral.agendakeun.data.model.Event
import com.airmineral.agendakeun.data.model.Group
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import java.util.*

class EventRepository {
    companion object {
        const val TAG = "Event Repo"
    }

    suspend fun saveGroupEvent(groupId: String, event: Event) {
        try {
            FirebaseInstance.groupEventColRef(groupId).document().set(event).await()
        } catch (e: FirebaseFirestoreException) {
            Log.d(TAG, e.message!!)
        }
    }

    suspend fun getAllEventList(): LiveData<List<Event>>? {
        return try {
            val allEventList = MutableLiveData<List<Event>>()
            val groupList = mutableListOf<Group>()
            val currentUserID = auth.currentUser?.uid
            val field = "userList.$currentUserID"
            val curDate = Calendar.getInstance().time
            FirebaseInstance.groupColRef.whereEqualTo(field, true)
                .get().await().forEach {
                    groupList.add(it.toObject())
                }
            val eventList = mutableListOf<Event>()
            groupList.forEach {
                FirebaseInstance.groupEventColRef(it.groupId!!)
                    .whereGreaterThanOrEqualTo("date", curDate)
                    .get().await().forEach { its ->
                            eventList.add(its.toObject())
                    }
            }
            eventList.sortBy { it.date }
            allEventList.postValue(eventList)
            allEventList
        } catch (e: FirebaseFirestoreException) {
            Log.d(TAG, e.message!!)
            null
        }
    }

    suspend fun getPastEventList(): LiveData<List<Event>>? {
        return try {
            val allEventList = MutableLiveData<List<Event>>()
            val groupList = mutableListOf<Group>()
            val currentUserID = auth.currentUser?.uid
            val field = "userList.$currentUserID"
            FirebaseInstance.groupColRef.whereEqualTo(field, true)
                .get().await().forEach {
                    groupList.add(it.toObject())
                }
            val eventList = mutableListOf<Event>()
            groupList.forEach {
                FirebaseInstance.groupEventColRef(it.groupId!!)
                    .get().await().forEach { its ->
                        eventList.add(its.toObject())
                    }
            }
            eventList.sortByDescending { it.date }
            allEventList.postValue(eventList)
            allEventList
        } catch (e: FirebaseFirestoreException) {
            Log.d(TAG, e.message!!)
            null
        }
    }

}