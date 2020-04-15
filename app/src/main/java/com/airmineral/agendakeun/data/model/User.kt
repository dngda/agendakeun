package com.airmineral.agendakeun.data.model

data class User(
    var uid: String,
    var name: String,
    var email: String,
    var avatar: String? = null,
    var position: String? = null
)