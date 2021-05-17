package com.airmineral.agendakeun.ui.event.create

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.airmineral.agendakeun.R
import com.airmineral.agendakeun.data.model.GroupItem
import com.airmineral.agendakeun.databinding.FragmentGroupChooserBinding
import com.airmineral.agendakeun.util.Coroutines
import com.airmineral.agendakeun.util.setInvisible
import com.airmineral.agendakeun.util.toGroupItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import org.koin.androidx.viewmodel.ext.android.viewModel

class GroupChooserFragment : Fragment() {

    private val viewModel: CreateEventViewModel by viewModel()
    private lateinit var binding: FragmentGroupChooserBinding
    private var isFromProfile: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_group_chooser, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
        isFromProfile = arguments?.getBoolean("isFromProfile", false)!!

        binding.btnGchCreate.setOnClickListener {
            if (isFromProfile) {
                val bundle = bundleOf("isFromProfile" to true)
                it.findNavController()
                    .navigate(R.id.action_groupManager_to_groupCreatorFragment2, bundle)
            } else {
                val bundle = bundleOf("isFromProfile" to false)
                it.findNavController()
                    .navigate(R.id.action_groupChooserFragment_to_groupCreatorFragment, bundle)
            }
        }
    }

    private fun bindUI() = Coroutines.main {
        try {
            viewModel.allGroupList.await().observe(viewLifecycleOwner, {
                initRecyclerView(it.toGroupItem())
                Log.d("Chooser Fragment", it.toString())

                if (it.isEmpty())
                    binding.tvGchErrorInfo.text = getString(R.string.not_available)
                else
                    setInvisible(binding.tvGchErrorInfo)
            })
        } catch (e: Exception) {
            Log.d("Chooser Fragment", e.message!!)
        }
    }

    private fun initRecyclerView(groupItem: List<GroupItem>) {
        val mAdapter = GroupAdapter<ViewHolder>().apply {
            addAll(groupItem)
        }

        binding.rvGch.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }

        mAdapter.setOnItemClickListener { item, view ->
            val itemData = item as GroupItem
            val groupData = itemData.group
            val bundle = bundleOf("groupData" to groupData)
            if (isFromProfile) {
                view.findNavController()
                    .navigate(R.id.action_groupManager_to_manageGroupFragment, bundle)
            } else {
                view.findNavController()
                    .navigate(R.id.action_groupChooserFragment_to_createEventFragment, bundle)
            }

        }
    }
}
