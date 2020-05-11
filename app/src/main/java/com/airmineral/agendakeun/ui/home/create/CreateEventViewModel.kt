package com.airmineral.agendakeun.ui.home.create

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.airmineral.agendakeun.R
import com.airmineral.agendakeun.data.model.Group
import com.airmineral.agendakeun.data.model.User
import com.airmineral.agendakeun.data.repositories.GroupRepository
import com.airmineral.agendakeun.data.repositories.UserRepository
import com.airmineral.agendakeun.util.lazyDeferred
import com.airmineral.agendakeun.util.toast
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch

class CreateEventViewModel(
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository
) : ViewModel() {

    private val currentUserId = userRepository.getCurrentUser()?.uid

    val listOfAllUser: Deferred<LiveData<List<User>>> by lazyDeferred {
        userRepository.getAllUserList()!!
    }

    val listOfAllGroup: Deferred<LiveData<List<Group>>> by lazyDeferred {
        groupRepository.getAllGroups()!!
    }

    var groupName: String? = null
    val selectedUserId = mutableListOf<String>().apply {
        add(currentUserId!!)
    }

    var count = MutableLiveData<Int>().apply {
        value = selectedUserId.size
    }

    fun onSaveBtnClick(view: View) {
        val mapSelectedData = selectedUserId.map {
            it to true
        }.toMap()

        viewModelScope.launch {
            groupRepository.saveGroup(Group(null, groupName, mapSelectedData))
        }
        view.context.toast("Berhasil Disimpan!")
        view.findNavController().navigate(R.id.action_groupCreatorFragment_to_groupChooserFragment)
    }

    val groupData = MutableLiveData<Group>()

}
