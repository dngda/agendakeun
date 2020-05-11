package com.airmineral.agendakeun.data.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Group(
    @DocumentId
    val groupId: String? = null,
    var name: String? = null,
    var listUsers: Map<String, Boolean>? = null
) : Parcelable {
    constructor() : this("", "", null)
}