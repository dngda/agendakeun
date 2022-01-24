package com.airmineral.agendakeun.data.model

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    var uid: String? = null,
    var name: String? = null,
    var email: String? = null,
    var avatar: String? = null,
    var position: String? = null,
    var orgCode: String? = null,
    var groupList: List<String>? = null
) {
    constructor() : this(null, null, null, null, null, null, null)
}