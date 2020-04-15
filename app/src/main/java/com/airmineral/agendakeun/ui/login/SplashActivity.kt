package com.airmineral.agendakeun.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.airmineral.agendakeun.ui.MainActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var mViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
    }

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
