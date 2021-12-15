package com.airmineral.agendakeun.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.airmineral.agendakeun.R
import com.airmineral.agendakeun.data.model.UserItem
import com.airmineral.agendakeun.databinding.FragmentGroupManagerBinding
import com.airmineral.agendakeun.util.Coroutines
import com.airmineral.agendakeun.util.toUserItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import org.koin.androidx.viewmodel.ext.android.viewModel

class ManageGroupFragment : Fragment() {
    companion object {
        const val TAG = "ManageGroup"
    }

    private val viewModel: ProfileViewModel by viewModel()
    private lateinit var binding: FragmentGroupManagerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_group_manager, container, false)
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
                val bundle = bundleOf("groupDataEdit" to viewModel.groupData)
                findNavController().navigate(
                    R.id.action_manageGroupFragment_to_groupEditorFragment,
                    bundle
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun bindUI() = Coroutines.main {
        try {
            viewModel.groupCreatorName.await().observe(viewLifecycleOwner, {
                binding.mgCreator.text = this.getString(R.string.group_creator_name, it.name)
            })
            viewModel.count.postValue(0)
            viewModel.groupUserList.await().observe(viewLifecycleOwner, {
                viewModel.count.postValue(it.size)
                initRecyclerView(it.toUserItem())
                Log.d(TAG + "_BindUI try", it.toString())
            })
        } catch (e: Exception) {
            Log.d(TAG + "_BindUI catch", e.message!!)
        }
    }

    private fun initRecyclerView(userItem: List<UserItem>) {
        val mAdapter = GroupAdapter<ViewHolder>().apply {
            addAll(userItem)
        }

        binding.rvManageGroup.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }
    }
}
