package com.airmineral.agendakeun.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.airmineral.agendakeun.R
import com.airmineral.agendakeun.databinding.ActivityCompleteSignInBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class CompleteSignInActivity : AppCompatActivity() {
    private val mViewModel: AuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityCompleteSignInBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_complete_sign_in)
        binding.viewModel = mViewModel
        mViewModel.getCurrentUser()
    }
}
