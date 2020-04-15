package com.airmineral.agendakeun.util

import android.content.Context
import android.widget.Toast

fun Context.toast(msg: String) = msg.let {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}