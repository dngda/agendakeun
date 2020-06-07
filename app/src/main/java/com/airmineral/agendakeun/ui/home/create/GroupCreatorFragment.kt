package com.airmineral.agendakeun.ui.home.create

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.airmineral.agendakeun.R
import com.airmineral.agendakeun.data.model.UserItem
import com.airmineral.agendakeun.databinding.FragmentGroupCreatorBinding
import com.airmineral.agendakeun.util.Coroutines
import com.airmineral.agendakeun.util.setInvisible
import com.airmineral.agendakeun.util.setVisible
import com.airmineral.agendakeun.util.toUserItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_group_creator.*
import kotlinx.android.synthetic.main.item_group_creator.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class GroupCreatorFragment : Fragment() {
    private val viewModel: CreateEventViewModel by viewModel()
    private lateinit var binding: FragmentGroupCreatorBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_group_creator, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindUI()
        viewModel.isFromProfile = arguments?.getBoolean("isFromProfile")!!
    }

    private fun bindUI() = Coroutines.main {
        try {
            viewModel.allUserList.await().observe(viewLifecycleOwner, Observer {
                initRecyclerView(it.toUserItem())

                setInvisible(tv_gct_errorInfo)
            })
        } catch (e: Exception) {
            Log.d("GCTF", e.message!!)
        }
    }

    private fun initRecyclerView(userItem: List<UserItem>) {
        val mAdapter = GroupAdapter<ViewHolder>().apply {
            addAll(userItem)
        }

        rv_gct.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }

        mAdapter.setOnItemClickListener { item, view ->
            val itemData = item as UserItem
            if (view.gct_checked.visibility == View.INVISIBLE) {
                setVisible(view.gct_checked)
                view.item_gct_background.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorPrimaryVeryLight
                    )
                )

                viewModel.selectedUserId.add(itemData.user.uid!!)
                viewModel.count.value = viewModel.selectedUserId.size
            } else {
                setInvisible(view.gct_checked)
                view.item_gct_background.background = null

                viewModel.selectedUserId.remove(itemData.user.uid!!)
                viewModel.count.value = viewModel.selectedUserId.size
            }
        }
    }


}
