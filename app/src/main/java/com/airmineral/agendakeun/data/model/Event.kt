package com.airmineral.agendakeun.data.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Event(
    @DocumentId
    var eventId: String? = null,
    var groupId: String? = null,
    var groupName: String? = null,
    var name: String? = null,
    var date: Date? = null,
    var place: String? = null,
    var desc: String? = null,
    var creator: String? = null
) : Parcelable {
    constructor() : this("", "", "", "", null, "", "", "")
}