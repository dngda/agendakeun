package com.airmineral.agendakeun.data

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FirebaseInstance {
    private val db = Firebase.firestore
    val auth = Firebase.auth
    val userColRef = db.collection("users")
    val groupColRef = db.collection("groups")
    fun groupEventColRef(groupId: String) = groupColRef.document(groupId).collection("events")
}