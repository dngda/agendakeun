package com.airmineral.agendakeun.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.airmineral.agendakeun.ui.MainActivity
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashActivity : AppCompatActivity() {

    private val mViewModel: AuthViewModel by viewModel()

    private fun shouldStartSignIn(): Boolean {
        return (!mViewModel.isSigningIn && FirebaseAuth.getInstance().currentUser == null)
    }

    override fun onStart() {
        super.onStart()
        if (shouldStartSignIn()) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            return
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
