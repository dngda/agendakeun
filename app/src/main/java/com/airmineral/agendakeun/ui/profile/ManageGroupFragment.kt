package com.airmineral.agendakeun.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.airmineral.agendakeun.R
import com.airmineral.agendakeun.data.model.UserItem
import com.airmineral.agendakeun.databinding.FragmentManageGroupBinding
import com.airmineral.agendakeun.util.Coroutines
import com.airmineral.agendakeun.util.toUserItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_manage_group.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ManageGroupFragment : Fragment() {
    companion object {
        const val TAG = "ManageGroup"
    }

    private val viewModel: ProfileViewModel by viewModel()
    private lateinit var binding: FragmentManageGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_manage_group, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.groupData = arguments?.getParcelable("groupData")

        bindUI()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_manage_group, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_edit -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun bindUI() = Coroutines.main {
        try {
            viewModel.groupUserList.await().observe(viewLifecycleOwner, Observer {
                viewModel.count.postValue(it.size)
                initRecyclerView(it.toUserItem())
                Log.d(TAG, it.toString())
            })
        } catch (e: Exception) {
            Log.d(TAG, e.message!!)
        }
    }

    private fun initRecyclerView(userItem: List<UserItem>) {
        val mAdapter = GroupAdapter<ViewHolder>().apply {
            addAll(userItem)
        }

        rv_manage_group.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }
    }
}
