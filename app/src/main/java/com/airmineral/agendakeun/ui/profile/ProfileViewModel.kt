package com.airmineral.agendakeun.ui.profile

import android.content.Intent
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.airmineral.agendakeun.R
import com.airmineral.agendakeun.data.model.Group
import com.airmineral.agendakeun.data.model.User
import com.airmineral.agendakeun.data.repositories.GroupRepository
import com.airmineral.agendakeun.data.repositories.UserRepository
import com.airmineral.agendakeun.ui.login.SignInActivity
import com.airmineral.agendakeun.util.lazyDeferred
import kotlinx.coroutines.Deferred

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository
) : ViewModel() {

    private val userLiveData by lazy {
        return@lazy userRepository.getUserData()
    }

    val user: LiveData<User> = userLiveData

    fun startSignOut(view: View) {
        userRepository.signOut()

        Intent(view.context, SignInActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            view.context.startActivity(it)
        }
    }

    fun startManageGroup(view: View) {
        val bundle = bundleOf("isFromProfile" to true)
        view.findNavController().navigate(R.id.action_profileFragment_to_groupManager, bundle)
    }

    var groupData: Group? = null
    val count = MutableLiveData<Int>()

    val groupUserList: Deferred<LiveData<List<User>>> by lazyDeferred {
        groupRepository.getGroupUserList(groupData?.groupId!!)!!
    }

}