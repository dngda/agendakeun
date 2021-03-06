package com.airmineral.agendakeun.ui.home.create

import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.airmineral.agendakeun.R
import com.airmineral.agendakeun.data.model.Event
import com.airmineral.agendakeun.data.model.Group
import com.airmineral.agendakeun.data.model.User
import com.airmineral.agendakeun.data.repositories.EventRepository
import com.airmineral.agendakeun.data.repositories.GroupRepository
import com.airmineral.agendakeun.data.repositories.UserRepository
import com.airmineral.agendakeun.notification.sendNotificationToServer
import com.airmineral.agendakeun.util.lazyDeferred
import com.airmineral.agendakeun.util.toast
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CreateEventViewModel(
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository,
    private val eventRepository: EventRepository
) : ViewModel() {

    private val currentUser = userRepository.getCurrentUser()
    private val currentUserId = currentUser?.uid
    var isFromProfile = false

    val allUserList: Deferred<LiveData<List<User>>> by lazyDeferred {
        userRepository.getAllUserList()!!
    }

    val allGroupList: Deferred<LiveData<List<Group>>> by lazyDeferred {
        groupRepository.getAllGroups()!!
    }

    var groupName: String? = null
    val selectedUserId = mutableListOf<String>().apply {
        add(currentUserId!!)
    }

    var count = MutableLiveData<Int>().apply {
        value = selectedUserId.size
    }

    fun onSaveGroupBtnClick(view: View) {
        val mapSelectedData = selectedUserId.map {
            it to true
        }.toMap()

        viewModelScope.launch {
            groupRepository.saveGroup(Group(null, groupName, mapSelectedData))
        }
        view.context.toast("Berhasil Disimpan!")
        if (isFromProfile) {
            val bundle = bundleOf("isFromProfile" to true)
            view.findNavController()
                .navigate(R.id.action_groupCreatorFragment2_to_groupManager, bundle)
        } else {
            val bundle = bundleOf("isFromProfile" to false)
            view.findNavController()
                .navigate(R.id.action_groupCreatorFragment_to_groupChooserFragment, bundle)
        }
    }

    val groupData = MutableLiveData<Group>()

    var eventName: String? = null
    var eventPlace: String? = null
    var eventDesc: String? = null
    var eventDateAndTime: Date? = null

    fun onSaveEventBtnClick(view: View) {
        viewModelScope.launch {
            eventRepository.saveGroupEvent(
                groupData.value?.groupId!!,
                Event(
                    null,
                    groupData.value?.groupId,
                    groupData.value?.name,
                    eventName,
                    eventDateAndTime,
                    eventPlace,
                    eventDesc,
                    currentUser?.displayName
                )
            )
        }
        val sdFormat = SimpleDateFormat("EEEE, dd MMM yyyy 'pukul' HH:mm", Locale.getDefault())
        val dateTime = sdFormat.format(eventDateAndTime!!)
        sendNotificationToServer(
            view.context, groupData.value?.groupId!!,
            "Agenda baru untuk ${groupData.value?.name} ditambahkan!",
            "$eventName pada hari $dateTime bertempat di $eventPlace."
        )
        view.findNavController().navigate(R.id.action_createEventFragment_to_homeFragment)
    }
}
