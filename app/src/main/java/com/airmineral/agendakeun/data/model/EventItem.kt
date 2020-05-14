package com.airmineral.agendakeun.data.model

import com.airmineral.agendakeun.R
import com.airmineral.agendakeun.databinding.ItemEventBinding
import com.xwray.groupie.databinding.BindableItem

class EventItem(val event: Event) : BindableItem<ItemEventBinding>() {
    override fun getLayout(): Int = R.layout.item_event

    override fun bind(viewBinding: ItemEventBinding, position: Int) {
        viewBinding.event = event
    }
}