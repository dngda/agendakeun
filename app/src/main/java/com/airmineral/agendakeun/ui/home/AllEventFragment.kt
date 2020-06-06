package com.airmineral.agendakeun.ui.home

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
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
import kotlinx.android.synthetic.main.fragment_all_event.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class AllEventFragment : Fragment() {
    companion object {
        private const val TAG = "All Event Fragment"
        private const val LIST_STATE_KEY = "AllEF"
    }

    private val viewModel: HomeViewModel by viewModel()
    private lateinit var mListState: Parcelable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_all_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindUI()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        mListState = all_event_rv.layoutManager?.onSaveInstanceState()!!
        outState.putParcelable(LIST_STATE_KEY, mListState)
    }

    override fun onResume() {
        super.onResume()
        all_event_rv.layoutManager?.onRestoreInstanceState(mListState)
    }
    private fun bindUI() = Coroutines.main {
        try {
            viewModel.pastEventList.await().observe(viewLifecycleOwner, Observer {
                initRecyclerView(it.toEventItem())
                Log.d(TAG, it.toString())

                if (it.isEmpty())
                    all_event_info.text = getString(R.string.tv_event_not_available)
                else {
                    setInvisible(all_event_info)
                    setInvisible(all_event_art)
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

        all_event_rv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }

        mAdapter.setOnItemClickListener { item, view ->
            val itemData = item as EventItem
            val eventData = itemData.event
            val bundle = bundleOf("eventData" to eventData)
            view.findNavController()
                .navigate(R.id.action_allEventFragment_to_eventDetailFragment, bundle)
        }
    }

}
