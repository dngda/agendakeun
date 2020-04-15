package com.airmineral.agendakeun.data.repositories

import androidx.lifecycle.MutableLiveData
import com.airmineral.agendakeun.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {

    var user: MutableLiveData<User> = MutableLiveData()
    var db = FirebaseFirestore.getInstance()
    private var auth = FirebaseAuth.getInstance()

    fun insertNewUser(uid: String) {

    }

    fun signOut() {
        auth.signOut()
    }
}