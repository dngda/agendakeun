package com.airmineral.agendakeun.ui.login

import android.content.Intent
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.airmineral.agendakeun.data.model.User
import com.airmineral.agendakeun.data.repositories.UserRepository
import com.airmineral.agendakeun.ui.MainActivity
import com.airmineral.agendakeun.util.Coroutines
import com.google.firebase.auth.FirebaseUser

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    var isSigningIn = false
    private var currentUser: FirebaseUser? = null

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    fun getCurrentUser() {
        currentUser = userRepository.getCurrentUser()
        _user.value = User(
            currentUser!!.uid,
            currentUser!!.displayName,
            currentUser!!.email,
            currentUser!!.photoUrl.toString(),
            ""
        )
    }

    fun saveNewUser(view: View) {
        Coroutines.io {
            userRepository.saveNewUser(user)
        }

        Intent(view.context, MainActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            view.context.startActivity(it)
        }
    }
}