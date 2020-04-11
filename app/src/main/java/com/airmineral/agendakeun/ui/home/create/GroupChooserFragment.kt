package com.airmineral.agendakeun.ui.home.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.airmineral.agendakeun.R
import kotlinx.android.synthetic.main.fragment_group_chooser.*

class GroupChooserFragment : Fragment() {

    companion object {
        fun newInstance() = GroupChooserFragment()
    }

    private lateinit var viewModel: CreateEventViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_group_chooser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(CreateEventViewModel::class.java)

        btn_gch_create.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_groupChooserFragment_to_groupCreatorFragment)
        }
    }

}
