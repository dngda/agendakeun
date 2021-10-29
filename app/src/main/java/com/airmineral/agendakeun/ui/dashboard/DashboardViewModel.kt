package com.airmineral.agendakeun.ui.dashboard

import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.airmineral.agendakeun.R
import com.airmineral.agendakeun.data.model.Event
import com.airmineral.agendakeun.data.model.User
import com.airmineral.agendakeun.data.repositories.EventRepository
import com.airmineral.agendakeun.data.repositories.UserRepository
import com.airmineral.agendakeun.util.lazyDeferred
import kotlinx.coroutines.Deferred

class DashboardViewModel(
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository
) : ViewModel() {

    val allEventList: Deferred<LiveData<List<Event>>> by lazyDeferred {
        eventRepository.getPastEventList()!!
    }
    val eventList: Deferred<LiveData<List<Event>>> by lazyDeferred {
        eventRepository.getCurEventList()!!
    }

    private val userLiveData by lazy {
        return@lazy userRepository.getUserData()
    }

    val upEvent = MutableLiveData<Event>()
    val user: LiveData<User> = userLiveData

    val statAllEvent = MutableLiveData<String>()
    val statPassedEvent = MutableLiveData<String>()
    val statNextEvent = MutableLiveData<String>()

    fun onUpcomingEventClicked(view: View) {
        if (!upEvent.value?.eventId.isNullOrBlank()) {
            val eventData = upEvent.value!!
            val bundle = bundleOf("eventData" to eventData)
            view.findNavController()
                .navigate(R.id.action_dashboardFragment_to_eventDetailFragment2, bundle)
        }

    }
}
