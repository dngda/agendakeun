package com.airmineral.agendakeun.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.airmineral.agendakeun.R
import com.airmineral.agendakeun.data.model.Event
import com.airmineral.agendakeun.data.preferences.PreferenceProvider
import com.airmineral.agendakeun.databinding.FragmentEventDetailBinding
import com.airmineral.agendakeun.notification.NotificationWorker
import com.airmineral.agendakeun.util.toast
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import java.util.concurrent.TimeUnit


class EventDetailFragment : Fragment() {
    private lateinit var binding: FragmentEventDetailBinding
    private val viewModel: HomeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_event_detail, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventData: Event = arguments?.getParcelable("eventData")!!
        viewModel.eventData.value = eventData

        viewModel.isSwitchChecked.value =
            PreferenceProvider(requireContext()).getSwitchState(eventData.eventId!!)

        val descOneHourBefore =
            "Agenda ${eventData.name} untuk ${eventData.groupName} akan dimulai satu jam lagi. Bertempat di ${eventData.place}."
        val descAtEventTime =
            "Agenda ${eventData.name} untuk ${eventData.groupName} sedang berlangsung. Bertempat di ${eventData.place}."
        val eventTime = eventData.date?.time!!.minus(Calendar.getInstance().timeInMillis)
        val oneHourBefore = eventTime - 3600000L
        val eventNotificationTime = if (oneHourBefore > 0L) {
            oneHourBefore
        } else {
            eventTime
        }
        if (eventTime < 0L) {
            binding.switchNotification.isEnabled = false
        }
        binding.switchNotification.setOnClickListener {
            if (binding.switchNotification.isChecked) {
                PreferenceProvider(requireContext()).saveSwitchState(eventData.eventId!!, true)
                val myWorkOneHourBefore = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInitialDelay(eventNotificationTime, TimeUnit.MILLISECONDS)
                    .addTag(eventData.eventId!!)
                    .setInputData(workDataOf("DESC" to descOneHourBefore))
                    .build()

                val myWorkAtEventTime = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInitialDelay(eventTime, TimeUnit.MILLISECONDS)
                    .addTag(eventData.eventId!!)
                    .setInputData(workDataOf("DESC" to descAtEventTime))
                    .build()

                WorkManager.getInstance(requireContext()).enqueue(myWorkOneHourBefore)
                WorkManager.getInstance(requireContext()).enqueue(myWorkAtEventTime)
                requireContext().toast("Notification on.")
            } else {
                PreferenceProvider(requireContext()).saveSwitchState(eventData.eventId!!, false)
                WorkManager.getInstance(requireContext()).cancelAllWorkByTag(eventData.eventId!!)
                requireContext().toast("Notification off.")
            }
        }

    }

}
