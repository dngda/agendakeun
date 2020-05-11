package com.airmineral.agendakeun.ui.home.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.airmineral.agendakeun.R
import com.airmineral.agendakeun.data.model.Group
import com.airmineral.agendakeun.databinding.FragmentCreateEventBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

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
    }
}
