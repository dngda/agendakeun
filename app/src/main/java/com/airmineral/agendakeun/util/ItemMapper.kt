package com.airmineral.agendakeun.util

import com.airmineral.agendakeun.data.model.*

fun List<Event>.toEventItem(): List<EventItem> {
    return this.map {
        EventItem(it)
    }
}

fun List<User>.toUserItem(): List<UserItem> {
    return this.map {
        UserItem(it)
    }
}

fun List<Group>.toGroupItem(): List<GroupItem> {
    return this.map {
        GroupItem(it)
    }
}