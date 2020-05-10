package com.airmineral.agendakeun.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.airmineral.agendakeun.R
import com.bumptech.glide.Glide

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
        .placeholder(R.drawable.ic_person_24dp)
        .into(iv)
}