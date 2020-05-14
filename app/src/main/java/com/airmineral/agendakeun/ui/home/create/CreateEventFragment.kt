package com.airmineral.agendakeun.ui.home.create

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.airmineral.agendakeun.R
import com.airmineral.agendakeun.data.model.Group
import com.airmineral.agendakeun.databinding.FragmentCreateEventBinding
import kotlinx.android.synthetic.main.fragment_create_event.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class CreateEventFragment : Fragment() {
    private val viewModel: CreateEventViewModel by viewModel()
    private lateinit var binding: FragmentCreateEventBinding
    private lateinit var groupData: Group

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_create_event, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.groupData.value = arguments?.getParcelable("groupData")!!

        initDateTimePicker()
    }

    private fun initDateTimePicker() {
        val cal = Calendar.getInstance()

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "EEEE, dd MMM yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
                new_event_date.setText(sdf.format(cal.time))
                viewModel.eventDateAndTime = cal.time
            }

        val timeSetListener =
            TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                cal.set(Calendar.SECOND, 0)
                new_event_time.setText(
                    SimpleDateFormat(
                        "HH:mm",
                        Locale.getDefault()
                    ).format(cal.time)
                )
                viewModel.eventDateAndTime = cal.time
            }

        new_event_date.setOnClickListener {
            DatePickerDialog(
                it.context, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        new_event_time.setOnClickListener {
            TimePickerDialog(
                it.context,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }
    }
}
