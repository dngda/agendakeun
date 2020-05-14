package com.airmineral.agendakeun.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.airmineral.agendakeun.data.model.Event
import com.airmineral.agendakeun.data.repositories.EventRepository
import com.airmineral.agendakeun.util.lazyDeferred
import kotlinx.coroutines.Deferred

class HomeViewModel(private val eventRepository: EventRepository) : ViewModel() {

    val eventList: Deferred<LiveData<List<Event>>> by lazyDeferred {
        eventRepository.getAllEventList()!!
    }
}