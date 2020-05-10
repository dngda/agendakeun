package com.airmineral.agendakeun.util

import android.content.Context
import android.view.View
import android.widget.Toast

fun Context.toast(msg: String) = msg.let {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun setInvisible(view: View) {
    view.visibility = View.INVISIBLE
}

fun setVisible(view: View) {
    view.visibility = View.VISIBLE
}