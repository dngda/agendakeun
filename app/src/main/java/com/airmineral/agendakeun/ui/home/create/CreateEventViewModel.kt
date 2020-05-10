package com.airmineral.agendakeun.ui.home.create

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.airmineral.agendakeun.data.model.User
import com.airmineral.agendakeun.data.repositories.UserRepository
import com.airmineral.agendakeun.util.lazyDeferred
import kotlinx.coroutines.Deferred

class CreateEventViewModel(private val userRepository: UserRepository) : ViewModel() {

    val listOfAllUser: Deferred<LiveData<List<User>>> by lazyDeferred {
        userRepository.getAllUserList()!!
    }
    var groupName: String? = null
    val selectedUserId = mutableListOf<String>()
    val mapSelectedData = selectedUserId.map {
        it to true
    }.toMap()

    fun onSaveBtnClick(view: View) {

    }
}
