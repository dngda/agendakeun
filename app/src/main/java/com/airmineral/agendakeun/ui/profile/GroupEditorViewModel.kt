package com.airmineral.agendakeun.ui.profile

import android.app.AlertDialog
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.airmineral.agendakeun.R
import com.airmineral.agendakeun.data.model.Group
import com.airmineral.agendakeun.data.model.User
import com.airmineral.agendakeun.data.repositories.GroupRepository
import com.airmineral.agendakeun.data.repositories.UserRepository
import com.airmineral.agendakeun.util.lazyDeferred
import kotlinx.coroutines.Deferred

class GroupEditorViewModel(
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository
) : ViewModel() {

    var groupData: Group? = null

    val groupMember: Deferred<LiveData<List<User>>> by lazyDeferred {
        groupRepository.getGroupUserList(groupData?.groupId!!, true)!!
    }
    val allUserExcMember: Deferred<LiveData<List<User>>> by lazyDeferred {
        userRepository.getAllUserExcMember(groupData?.groupId)!!
    }

    val memberUid = mutableListOf<String>()
    val selectedForDelete = mutableListOf<String>()
    val selectedForAdd = mutableListOf<String>()

    fun onSaveGroupBtnClick(view: View) {
        //TODO hapus creator di seleksi untuk hapus anggota
        memberUid.addAll(selectedForAdd)
        memberUid.removeAll(selectedForDelete)
        val mapUserList = memberUid.map {
            it to true
        }.toMap()
        groupRepository.editGroup(groupData!!, mapUserList, selectedForDelete, selectedForAdd)
        Toast.makeText(view.context, "Edited", Toast.LENGTH_SHORT).show()
        val bundle = bundleOf("isFromProfile" to true)
        view.findNavController().navigate(
            R.id.action_groupEditorFragment_to_groupManager, bundle
        )
    }

    fun onDeleteGroupBtnClick(view: View) {
        val alertYesNo = AlertDialog.Builder(view.context).apply {
            setTitle("Hapus Kelompok?")
            setMessage("Menghapus kelompok akan menghapus semua agenda terkait! Lanjutkan?")
            setPositiveButton("Oke") { _, _ ->
                groupRepository.deleteGroup(groupData?.groupId!!)
                Toast.makeText(
                    view.context,
                    "Kelompok dan semua agenda terkait akan dihapus",
                    Toast.LENGTH_SHORT
                ).show()
                val bundle = bundleOf("isFromProfile" to true)
                view.findNavController().navigate(
                    R.id.action_groupEditorFragment_to_groupManager, bundle
                )
            }
            setNegativeButton("Batal") { _, _ ->
                Toast.makeText(view.context, "Batal", Toast.LENGTH_SHORT).show()
            }
        }
        val alertNotCreator = AlertDialog.Builder(view.context).apply {
            setTitle("Bukan Pembuat!")
            setMessage("Hanya pembuat kelompok yang bisa menghapus kelompok!")
            setPositiveButton("Oke") { _, _ -> }
        }
        if (userRepository.getCurrentUser().uid == groupData?.creator) {
            alertYesNo.show()
        } else {
            alertNotCreator.show()
        }
    }

}