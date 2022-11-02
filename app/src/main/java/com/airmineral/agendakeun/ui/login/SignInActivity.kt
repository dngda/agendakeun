package com.airmineral.agendakeun.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.airmineral.agendakeun.BuildConfig
import com.airmineral.agendakeun.R
import com.airmineral.agendakeun.databinding.ActivitySignInBinding
import com.airmineral.agendakeun.ui.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignInActivity : AppCompatActivity() {
    private val mViewModel: AuthViewModel by viewModel()

    companion object {
        const val TAG = "SignInActivity"
    }

    private lateinit var binding: ActivitySignInBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("184531547133-3nara4jng21tbbn3idhi0t2nod77ungm.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()
        binding.btnWelcomeSignIn.setOnClickListener {
            signIn()
            binding.btnWelcomeSignIn.isEnabled = false
        }

    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        binding.welcomeProgressBar.visibility = View.VISIBLE

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    mViewModel.isSigningIn = true
                    val isNewUser = task.result?.additionalUserInfo?.isNewUser
                    if (isNewUser!!) {
                        startActivity(Intent(this, CompleteSignInActivity::class.java))
                        finish()
                    } else {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    showAuthFailed(task.exception)

                }
                binding.welcomeProgressBar.visibility = View.INVISIBLE
            }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed
                Log.w(TAG, "Google sign in failed", e)
                showAuthFailed(e)
                binding.btnWelcomeSignIn.isEnabled = true
            }
        }


    private fun showAuthFailed(msg: Exception?) {
        Snackbar.make(
            binding.rootSignIn,
            if (BuildConfig.DEBUG) msg.toString() else "Authentication Failed.",
            Snackbar.LENGTH_LONG
        )
            .show()
    }

}
