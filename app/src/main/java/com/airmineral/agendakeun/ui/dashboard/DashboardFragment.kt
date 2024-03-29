package com.airmineral.agendakeun.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.airmineral.agendakeun.R
import com.airmineral.agendakeun.data.model.Event
import com.airmineral.agendakeun.databinding.FragmentDashboardBinding
import com.airmineral.agendakeun.ui.event.HomeFragment
import com.airmineral.agendakeun.util.Coroutines
import org.koin.androidx.viewmodel.ext.android.viewModel

class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private val viewModel: DashboardViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.user.observe(viewLifecycleOwner, {
            if (it.name != "") {
                binding.isUserLoaded = true
            }
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindUI()
        binding.dashboardRefresh.setOnRefreshListener {
            bindUI()
        }
    }

    private fun bindUI() = Coroutines.main {
        var nextEventCount = 0
        try {
            viewModel.eventListAsync().await().observe(viewLifecycleOwner, {
                binding.dashboardRefresh.isRefreshing = false
                if (it.isEmpty()) {
                    viewModel.upEvent.value = Event(name = "No Event")
                    viewModel.statNextEvent.value = "0"
                } else {
                    viewModel.upEvent.value = it.first()
                    viewModel.statNextEvent.value = it.size.toString()
                    nextEventCount = it.size
                }
                binding.isNextLoaded = true
            })
            viewModel.allEventListAsync().await().observe(viewLifecycleOwner, {
                binding.dashboardRefresh.isRefreshing = false
                if (it.isEmpty()) {
                    viewModel.statPassedEvent.value = "0"
                } else {
                    binding.isAllStatLoaded = true
                    viewModel.statAllEvent.value = it.size.toString()
                    val allEventCount = it.size
                    viewModel.statPassedEvent.value = (allEventCount - nextEventCount).toString()
                }
            })

        } catch (e: Exception) {
            Log.d(HomeFragment.TAG, e.stackTraceToString())
        }
    }
}
