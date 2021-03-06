package com.airmineral.agendakeun.data.model

import com.airmineral.agendakeun.R
import com.airmineral.agendakeun.databinding.ItemGroupCreatorBinding
import com.xwray.groupie.databinding.BindableItem

class UserItem(val user: User) : BindableItem<ItemGroupCreatorBinding>() {
    override fun getLayout(): Int = R.layout.item_group_creator

    override fun bind(viewBinding: ItemGroupCreatorBinding, position: Int) {
        viewBinding.user = user
    }

}