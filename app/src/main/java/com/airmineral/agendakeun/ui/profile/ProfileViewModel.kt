package com.airmineral.agendakeun.ui.profile

import android.content.Intent
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.airmineral.agendakeun.data.model.User
import com.airmineral.agendakeun.data.repositories.UserRepository
import com.airmineral.agendakeun.ui.login.SignInActivity

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun user(): LiveData<User> = userRepository.getUserData()

    fun startSignOut(view: View) {
        userRepository.signOut()

        Intent(view.context, SignInActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            view.context.startActivity(it)
        }
    }
}