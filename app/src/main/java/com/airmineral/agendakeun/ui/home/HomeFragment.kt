package com.airmineral.agendakeun.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.airmineral.agendakeun.R
import com.airmineral.agendakeun.data.model.EventItem
import com.airmineral.agendakeun.util.Coroutines
import com.airmineral.agendakeun.util.setInvisible
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_home_add.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_groupChooserFragment)
        }
        fab.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_groupChooserFragment)
        }

        bindUI()
    }

    private fun updateUI() = Coroutines.main {
        try {
            viewModel.getEventListAsync().await().observe(viewLifecycleOwner, Observer {
                initRecyclerView(it.toEventItem())

                if (it.isNotEmpty())
                    home_refresh.isRefreshing = false
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
                    setInvisible(home_event_info)
                    setInvisible(home_event_art)
                    setInvisible(btn_home_add)
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
        }
    }
}