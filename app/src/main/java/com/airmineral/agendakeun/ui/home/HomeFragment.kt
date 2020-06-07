package com.airmineral.agendakeun.ui.home

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.airmineral.agendakeun.R
import com.airmineral.agendakeun.data.model.EventItem
import com.airmineral.agendakeun.util.Coroutines
import com.airmineral.agendakeun.util.setInvisible
import com.airmineral.agendakeun.util.setVisible
import com.airmineral.agendakeun.util.toEventItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {
    companion object {
        const val TAG = "Home Fragment"
    }

    private val viewModel: HomeViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = bundleOf("isFromProfile" to false)
        btn_home_add.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_homeFragment_to_groupChooserFragment, bundle)
        }
        fab.setOnClickListener {
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
                home_refresh.isRefreshing = false
                if (it.isEmpty()) {
                    home_event_info.text = getString(R.string.tv_event_not_available)
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
                    home_event_info.text = getString(R.string.tv_event_not_available)
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

        home_event_rv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }

        home_refresh.setOnRefreshListener {
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
        setInvisible(home_event_info)
        setInvisible(home_event_art)
        setInvisible(btn_home_add)
    }

    private fun setViewVisible() {
        setVisible(home_event_info)
        setVisible(home_event_art)
        setVisible(btn_home_add)
    }
}