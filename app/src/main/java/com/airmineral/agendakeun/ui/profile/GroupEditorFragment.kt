package com.airmineral.agendakeun.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.airmineral.agendakeun.R
import com.airmineral.agendakeun.data.model.UserItem
import com.airmineral.agendakeun.data.model.UserItemDelete
import com.airmineral.agendakeun.databinding.FragmentGroupEditorBinding
import com.airmineral.agendakeun.databinding.ItemGroupCreatorBinding
import com.airmineral.agendakeun.databinding.ItemGroupCreatorDeleteBinding
import com.airmineral.agendakeun.util.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import org.koin.androidx.viewmodel.ext.android.viewModel

class GroupEditorFragment : Fragment() {
    companion object {
        const val TAG = "GroupEditorFragment"
    }

    private val viewModel: GroupEditorViewModel by viewModel()
    private lateinit var binding: FragmentGroupEditorBinding
    private lateinit var itemBinding: ItemGroupCreatorBinding
    private lateinit var itemBindingDel: ItemGroupCreatorDeleteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_group_editor,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.groupData = arguments?.getParcelable("groupDataEdit")
        viewModel.memberUid.addAll(viewModel.groupData?.userList?.toList()!!.map {
            it.first
        })
        bindUI()
    }

    private fun bindUI() = Coroutines.main {
        try {
            viewModel.groupMember.await().observe(viewLifecycleOwner, {
                initRecyclerViewDelete(it.toUserItemDelete())

                setInvisible(binding.tvGeditLoadingRemove)
            })
            viewModel.allUserExcMember.await().observe(viewLifecycleOwner, {
                initRecyclerViewAdd(it.toUserItem())

                setInvisible(binding.tvGeditLoadingAdd)
            })
        } catch (e: Exception) {
            Log.d("Gedit BindUI", e.stackTraceToString())
        }
    }

    private fun initRecyclerViewDelete(userItem: List<UserItemDelete>) {
        val mAdapterRemove = GroupAdapter<ViewHolder>().apply {
            addAll(userItem)
        }

        binding.rvGeditRemove.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapterRemove
        }
        mAdapterRemove.setOnItemClickListener { item, view ->
            itemBindingDel = DataBindingUtil.getBinding(view)!!
            val itemDataDelete = item as UserItemDelete
            if (itemBindingDel.gctChecked.visibility == View.INVISIBLE) {
                setVisible(itemBindingDel.gctChecked)
                itemBindingDel.itemGctBackground.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorPrimaryVeryLight
                    )
                )
                viewModel.selectedForDelete.add(itemDataDelete.user.uid!!)
            } else {
                setInvisible(itemBindingDel.gctChecked)
                itemBindingDel.itemGctBackground.background = null
                viewModel.selectedForDelete.remove(itemDataDelete.user.uid!!)
            }
        }
    }

    private fun initRecyclerViewAdd(userItem: List<UserItem>) {
        val mAdapterAdd = GroupAdapter<ViewHolder>().apply {
            addAll(userItem)
        }

        binding.rvGeditAdd.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapterAdd
        }

        mAdapterAdd.setOnItemClickListener { item, view ->
            itemBinding = DataBindingUtil.getBinding(view)!!
            val itemData = item as UserItem

            if (itemBinding.gctChecked.visibility == View.INVISIBLE) {
                setVisible(itemBinding.gctChecked)
                itemBinding.itemGctBackground.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorPrimaryVeryLight
                    )
                )
                viewModel.selectedForAdd.add(itemData.user.uid!!)
            } else {
                setInvisible(itemBinding.gctChecked)
                itemBinding.itemGctBackground.background = null
                viewModel.selectedForAdd.remove(itemData.user.uid!!)
            }
        }
    }
}