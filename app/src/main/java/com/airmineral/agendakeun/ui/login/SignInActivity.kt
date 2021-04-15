package com.airmineral.agendakeun.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.airmineral.agendakeun.R
import com.airmineral.agendakeun.databinding.ActivitySignInBinding
import com.airmineral.agendakeun.ui.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
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
        const val EXTRA_KEY = "new_sign_uid"
        const val TAG = "SignInActivity"
        const val RC_SIGN_IN = 123
    }

    private lateinit var binding: ActivitySignInBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
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
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)
        binding.welcomeProgressBar.visibility = View.VISIBLE

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
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
                    Snackbar.make(
                        binding.rootSignIn,
                        "Authentication Failed.",
                        Snackbar.LENGTH_SHORT
                    )
                        .show()

                }

                binding.welcomeProgressBar.visibility = View.INVISIBLE
            }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed
                Log.w(TAG, "Google sign in failed", e)
                binding.btnWelcomeSignIn.isEnabled = true
            }
        }
    }

}
