package com.airmineral.agendakeun.util

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.airmineral.agendakeun.R
import com.airmineral.agendakeun.data.model.Event
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("app:loadImageWithGlide")
fun loadImage(iv: ImageView, url: String?) {
    Glide.with(iv.context)
        .load(url)
        .into(iv)
}

@BindingAdapter("app:loadAvatarWithGlide")
fun loadImageAvatar(iv: ImageView, url: String?) {
    Glide.with(iv.context)
        .load(url)
        .placeholder(R.drawable.ic_user)
        .apply(RequestOptions.circleCropTransform())
        .into(iv)
}

@BindingAdapter("app:textEventDate")
fun convertDate(view: TextView, mDate: Date?) {
    val myFormat = "dd\nMMM"
    val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
    var date = ""
    if (mDate !== null) {
        date = sdf.format(mDate)
    }
    view.text = date
}

@SuppressLint("SetTextI18n")
@BindingAdapter("app:textPlaceAndTime")
fun convertData(view: TextView, event: Event?) {
    val myFormat = "HH:mm"
    val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
    if (event != null) {
        if (event.date != null) {
            val date = sdf.format(event.date!!)
            view.text = "$date at ${event.place}"
        } else {
            view.text = ""
        }
    }
}

@BindingAdapter("app:textEventDetailDate")
fun convertDetailDate(view: TextView, mDate: Date) {
    val myFormat = "EEEE, dd MMMM yyyy"
    val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
    view.text = sdf.format(mDate)
}

@BindingAdapter("app:textEventDetailTime")
fun convertDetailTime(view: TextView, mDate: Date) {
    val myFormat = "HH:mm z"
    val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
    view.text = sdf.format(mDate)
}