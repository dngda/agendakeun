package com.airmineral.agendakeun.ui.event

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.airmineral.agendakeun.R
import com.airmineral.agendakeun.data.model.EventItem
import com.airmineral.agendakeun.databinding.FragmentAllEventBinding
import com.airmineral.agendakeun.util.Coroutines
import com.airmineral.agendakeun.util.setInvisible
import com.airmineral.agendakeun.util.toEventItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import org.koin.androidx.viewmodel.ext.android.viewModel

class AllEventFragment : Fragment() {
    companion object {
        private const val TAG = "All Event Fragment"
        private const val LIST_STATE_KEY = "AllEF"
    }

    private lateinit var binding: FragmentAllEventBinding
    private val viewModel: HomeViewModel by viewModel()
    private lateinit var mListState: Parcelable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_all_event, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindUI()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        mListState = binding.allEventRv.layoutManager?.onSaveInstanceState()!!
        outState.putParcelable(LIST_STATE_KEY, mListState)
    }

    override fun onResume() {
        super.onResume()
        binding.allEventRv.layoutManager?.onRestoreInstanceState(mListState)
    }
    private fun bindUI() = Coroutines.main {
        try {
            viewModel.pastEventList.await().observe(viewLifecycleOwner, Observer {
                initRecyclerView(it.toEventItem())
                Log.d(TAG, it.toString())

                if (it.isEmpty())
                    binding.allEventInfo.text = getString(R.string.tv_event_not_available)
                else {
                    setInvisible(binding.allEventInfo)
                    setInvisible(binding.allEventArt)
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

        binding.allEventRv.apply {
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
