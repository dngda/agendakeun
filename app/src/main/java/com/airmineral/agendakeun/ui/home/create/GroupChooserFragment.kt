package com.airmineral.agendakeun.ui.home.create

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.airmineral.agendakeun.R
import com.airmineral.agendakeun.data.model.Group
import com.airmineral.agendakeun.databinding.FragmentGroupChooserBinding
import com.airmineral.agendakeun.util.Coroutines
import com.airmineral.agendakeun.util.setInvisible
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_group_chooser.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class GroupChooserFragment : Fragment() {

    private val viewModel: CreateEventViewModel by viewModel()
    private lateinit var binding: FragmentGroupChooserBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_group_chooser, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()

        btn_gch_create.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_groupChooserFragment_to_groupCreatorFragment)
        }
    }

    private fun bindUI() = Coroutines.main {
        try {
            viewModel.listOfAllGroup.await().observe(viewLifecycleOwner, Observer {
                initRecyclerView(it.toGroupItem())
                Log.d("Chooser Fragment", it.toString())

                setInvisible(tv_gch_errorInfo)
            })
        } catch (e: Exception) {
            Log.d("Chooser Fragment", e.message!!)
        }
    }

    private fun initRecyclerView(groupItem: List<GroupItem>) {
        val mAdapter = GroupAdapter<ViewHolder>().apply {
            addAll(groupItem)
        }

        rv_gch.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }
    }
}

private fun List<Group>.toGroupItem(): List<GroupItem> {
    return this.map {
        GroupItem(it)
    }
}
