package com.airmineral.agendakeun.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.airmineral.agendakeun.data.model.Event
import com.airmineral.agendakeun.data.model.User
import com.airmineral.agendakeun.data.repositories.UserRepository

class DashboardViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val userLiveData by lazy {
        return@lazy userRepository.getUserData()
    }

    val upEvent = Event()
    val user: LiveData<User> = userLiveData

}