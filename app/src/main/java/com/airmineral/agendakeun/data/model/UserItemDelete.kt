package com.airmineral.agendakeun.data.model

import com.airmineral.agendakeun.R
import com.airmineral.agendakeun.databinding.ItemGroupCreatorDeleteBinding
import com.xwray.groupie.databinding.BindableItem

class UserItemDelete(val user: User) : BindableItem<ItemGroupCreatorDeleteBinding>() {
    override fun getLayout(): Int = R.layout.item_group_creator_delete

    override fun bind(viewBinding: ItemGroupCreatorDeleteBinding, position: Int) {
        viewBinding.user = user
    }

}