package com.airmineral.agendakeun.ui.login

import android.content.Intent
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airmineral.agendakeun.data.model.User
import com.airmineral.agendakeun.data.repositories.UserRepository
import com.airmineral.agendakeun.ui.MainActivity
import com.airmineral.agendakeun.util.toast
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    var isSigningIn = false
    private var currentUser: FirebaseUser? = null

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    fun getNewUser() {
        currentUser = userRepository.getCurrentUser()
        _user.value = User(
            currentUser!!.uid,
            currentUser!!.displayName,
            currentUser!!.email,
            currentUser!!.photoUrl.toString(),
            ""
        )
    }

    private suspend fun saveUserData(): Boolean {
        val result = viewModelScope.async {
            userRepository.saveUser(user)
        }
        return result.await()
    }

    fun btnSaveNewUser(view: View) {
        viewModelScope.launch {
            val res = saveUserData()
            if (res) {
                view.context.toast("Berhasil disimpan!")
            } else {
                view.context.toast("Failed to save data!")
            }
        }
        Intent(view.context, MainActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            view.context.startActivity(it)
        }
    }
}