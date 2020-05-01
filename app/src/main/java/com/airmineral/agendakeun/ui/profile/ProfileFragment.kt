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
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {

    private val profileViewModel: ProfileViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentProfileBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        binding.viewModel = profileViewModel

        profileViewModel.user().observe(viewLifecycleOwner, Observer { user ->
            binding.profileName.text = user.name
            binding.profilePosition.text = user.position
            Glide.with(this)
                .load(user.avatar)
                .into(binding.profileAvatar)
        })
        return binding.root
    }
}
