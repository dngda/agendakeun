package com.airmineral.agendakeun.ui.login

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airmineral.agendakeun.data.model.User
import com.airmineral.agendakeun.data.preferences.PreferenceProvider
import com.airmineral.agendakeun.data.repositories.UserRepository
import com.airmineral.agendakeun.ui.MainActivity
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class AuthViewModel(
    private val userRepository: UserRepository,
    private val preferenceProvider: PreferenceProvider
) : ViewModel() {

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
            "",
            "",
            null
        )
    }

    private fun saveUserData() {
        viewModelScope.launch {
            userRepository.saveUser(user)
        }
        preferenceProvider.saveStrings("orgCode", user.value?.orgCode)
    }

    fun btnSaveNewUser(view: View) {
        if (user.value?.orgCode?.isEmpty()!!) {
            Toast.makeText(view.context, "Kode Organisasi tidak boleh kosong!", Toast.LENGTH_SHORT)
                .show()
        } else {
            saveUserData()
            Intent(view.context, MainActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                view.context.startActivity(it)
            }
        }
    }
}