package com.airmineral.agendakeun.data.model

import com.airmineral.agendakeun.R
import com.airmineral.agendakeun.databinding.ItemGroupChooserBinding
import com.xwray.groupie.databinding.BindableItem

class GroupItem(val group: Group) : BindableItem<ItemGroupChooserBinding>() {
    override fun getLayout(): Int = R.layout.item_group_chooser

    override fun bind(viewBinding: ItemGroupChooserBinding, position: Int) {
        viewBinding.group = group
    }
}