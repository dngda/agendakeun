package com.airmineral.agendakeun.ui.event

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.airmineral.agendakeun.R
import com.airmineral.agendakeun.data.model.EventItem
import com.airmineral.agendakeun.databinding.FragmentEventBinding
import com.airmineral.agendakeun.util.Coroutines
import com.airmineral.agendakeun.util.setInvisible
import com.airmineral.agendakeun.util.setVisible
import com.airmineral.agendakeun.util.toEventItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {
    companion object {
        const val TAG = "Home Fragment"
    }

    private lateinit var binding: FragmentEventBinding
    private val viewModel: HomeViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_event, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = bundleOf("isFromProfile" to false)
        binding.btnHomeAdd.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_homeFragment_to_groupChooserFragment, bundle)
        }
        binding.fab.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_homeFragment_to_groupChooserFragment, bundle)
        }

        bindUI()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home_items, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.allEvent -> findNavController().navigate(R.id.action_homeFragment_to_allEventFragment)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateUI() = Coroutines.main {
        try {
            viewModel.getEventListAsync().await().observe(viewLifecycleOwner, Observer {
                initRecyclerView(it.toEventItem())
                binding.homeRefresh.isRefreshing = false
                if (it.isEmpty()) {
                    binding.homeEventInfo.text = getString(R.string.tv_event_not_available)
                    setViewVisible()
                } else {
                    setViewInvisible()
                }
            })
        } catch (e: Exception) {
            Log.d(TAG, e.message!!)
        }
    }

    private fun bindUI() = Coroutines.main {
        try {
            viewModel.eventList.await().observe(viewLifecycleOwner, Observer {
                initRecyclerView(it.toEventItem())
                Log.d(TAG, it.toString())

                if (it.isEmpty())
                    binding.homeEventInfo.text = getString(R.string.tv_event_not_available)
                else {
                    setViewInvisible()
                }

            })
        } catch (e: Exception) {
            Log.d(TAG, e.message!!)
        }
    }

    private fun initRecyclerView(eventItem: List<EventItem>) {
        val mAdapter = GroupAdapter<ViewHolder>().apply {
            addAll(eventItem)
        }

        binding.homeEventRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }

        binding.homeRefresh.setOnRefreshListener {
            updateUI()
            mAdapter.update(eventItem)
        }
        mAdapter.setOnItemClickListener { item, view ->
            val itemData = item as EventItem
            val eventData = itemData.event
            val bundle = bundleOf("eventData" to eventData)
            view.findNavController()
                .navigate(R.id.action_homeFragment_to_eventDetailFragment, bundle)
        }
    }

    private fun setViewInvisible() {
        setInvisible(binding.homeEventInfo)
        setInvisible(binding.homeEventArt)
        setInvisible(binding.btnHomeAdd)
    }

    private fun setViewVisible() {
        setVisible(binding.homeEventInfo)
        setVisible(binding.homeEventArt)
        setVisible(binding.btnHomeAdd)
    }
}