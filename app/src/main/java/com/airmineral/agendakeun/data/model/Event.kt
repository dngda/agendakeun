package com.airmineral.agendakeun.data.model

import com.google.firebase.firestore.DocumentId
import java.util.*

data class Event(
    @DocumentId
    var eventId: String? = null,
    var groupId: String? = null,
    var groupName: String? = null,
    var name: String? = null,
    var date: Date? = null,
    var place: String? = null,
    var desc: String? = null
) {
    constructor() : this("", "", "", "", null, "", "")
}