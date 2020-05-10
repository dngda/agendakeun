package com.airmineral.agendakeun.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.airmineral.agendakeun.R
import com.airmineral.agendakeun.databinding.FragmentProfileBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {
    private val profileViewModel: ProfileViewModel by viewModel()
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        binding.viewModel = profileViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        profileViewModel.user.observe(viewLifecycleOwner, Observer {
            if (it.name != "") {
                binding.isLoaded = true
            }
        })
        return binding.root
    }
}
