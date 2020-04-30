package com.airmineral.agendakeun.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("app:loadImageWithGlide")
fun loadImage(iv: ImageView, url: String?) {
    Glide.with(iv.context)
        .load(url)
        .into(iv)
}