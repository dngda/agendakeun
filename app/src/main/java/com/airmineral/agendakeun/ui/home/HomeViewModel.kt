package com.airmineral.agendakeun.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airmineral.agendakeun.data.model.Event
import com.airmineral.agendakeun.data.repositories.EventRepository
import com.airmineral.agendakeun.util.lazyDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class HomeViewModel(private val eventRepository: EventRepository) : ViewModel() {

    val eventList: Deferred<LiveData<List<Event>>> by lazyDeferred {
        eventRepository.getAllEventList()!!
    }

    fun getEventListAsync(): Deferred<LiveData<List<Event>>> {
        return viewModelScope.async {
            eventRepository.getAllEventList()!!
        }
    }

    val eventData = MutableLiveData<Event>()
    val eventDetail: LiveData<Event> = eventData
}