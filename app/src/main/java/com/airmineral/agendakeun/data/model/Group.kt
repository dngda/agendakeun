package com.airmineral.agendakeun.data.model

data class Group(
    var name: String? = null,
    var listUsers: Map<String, Boolean>? = null
) {
    constructor() : this(null, null)
}