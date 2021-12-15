package com.airmineral.agendakeun.ui.event.create

import android.app.AlertDialog
import android.view.View
import android.widget.Toast
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
    private val currentUserId = currentUser.uid
    var isFromProfile = false

    val allUserList: Deferred<LiveData<List<User>>> by lazyDeferred {
        userRepository.getAllUserList()!!
    }

    val allGroupList: Deferred<LiveData<List<Group>>> by lazyDeferred {
        groupRepository.getAllGroups()!!
    }

    var groupName: String? = null
    val selectedUserId = mutableListOf<String>().apply {
        add(currentUserId)
    }

    var count = MutableLiveData<Int>().apply {
        value = selectedUserId.size
    }

    fun onSaveGroupBtnClick(view: View) {
        if (groupName.isNullOrEmpty()) return view.context.toast("Nama group tidak boleh kosong!")
        val mapSelectedData = selectedUserId.map {
            it to true
        }.toMap()

        groupRepository.saveGroup(
            Group(null, groupName, mapSelectedData, currentUserId),
            selectedUserId
        )

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

    var groupData = Group()
    var eventName: String? = null
    var eventPlace: String? = null
    var eventDesc: String? = null
    var eventID: String? = null
    var eventDateAndTime: Date? = null

    fun onDeleteEventBtnClick(view: View) {
        val alertYesNo = AlertDialog.Builder(view.context).apply {
            setTitle("Hapus Agenda?")
            setMessage("Anda yakin akan menghapus agenda ini?")
            setPositiveButton("Oke") { _, _ ->
                eventRepository.deleteGroupEvent(groupData.groupId!!, eventID!!)
                Toast.makeText(
                    view.context,
                    "Agenda terhapus",
                    Toast.LENGTH_SHORT
                ).show()
                view.findNavController().navigate(R.id.action_editEventFragment_to_eventFragment)
            }
            setNegativeButton("Batal") { _, _ ->
            }
        }
        alertYesNo.show()
    }

    fun onSaveEventBtnClick(view: View) {
        if (eventName.isNullOrEmpty()) return view.context.toast("Nama Agenda tidak boleh kosong!")
        if (eventPlace.isNullOrEmpty()) return view.context.toast("Tempat Agenda tidak boleh kosong!")
        saveEvent()
        val sdFormat = SimpleDateFormat("EEEE, dd MMM yyyy 'pukul' HH:mm", Locale.getDefault())
        val dateTime = sdFormat.format(eventDateAndTime!!)
        sendNotificationToServer(
            view.context, groupData.groupId!!,
            "Agenda baru untuk ${groupData.name} ditambahkan!",
            "$eventName pada hari $dateTime bertempat di $eventPlace."
        )
        view.findNavController().navigate(R.id.action_createEventFragment_to_homeFragment)
    }

    fun onEditEventBtnClick(view: View) {
        if (eventName.isNullOrEmpty()) return view.context.toast("Nama Agenda tidak boleh kosong!")
        if (eventPlace.isNullOrEmpty()) return view.context.toast("Tempat Agenda tidak boleh kosong!")
        eventRepository.deleteGroupEvent(groupData.groupId!!, eventID!!)
        saveEvent()
        val sdFormat = SimpleDateFormat("EEEE, dd MMM yyyy 'pukul' HH:mm", Locale.getDefault())
        val dateTime = sdFormat.format(eventDateAndTime!!)
        sendNotificationToServer(
            view.context, groupData.groupId!!,
            "Terjadi perubahan Agenda untuk ${groupData.name}",
            "$eventName pada hari $dateTime bertempat di $eventPlace."
        )
        view.findNavController().navigate(R.id.action_editEventFragment_to_eventFragment)
    }

    private fun saveEvent() {
        viewModelScope.launch {
            eventRepository.saveGroupEvent(
                groupData.groupId!!,
                Event(
                    null,
                    groupData.groupId,
                    groupData.name,
                    eventName,
                    eventDateAndTime,
                    eventPlace,
                    null,
                    eventDesc,
                    currentUser.displayName,
                    currentUserId
                )
            )
        }
    }
}
