package com.airmineral.agendakeun.ui

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.airmineral.agendakeun.R
import com.airmineral.agendakeun.data.FirebaseInstance
import com.airmineral.agendakeun.data.model.User
import com.airmineral.agendakeun.data.preferences.PreferenceProvider
import com.airmineral.agendakeun.data.repositories.UserRepository
import com.airmineral.agendakeun.util.Coroutines
import com.airmineral.agendakeun.util.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.android.ext.android.inject

class MainActivity() : AppCompatActivity(), Observer<User> {
    companion object {
        const val TAG = "MainActivity"
    }

    private val userRepository: UserRepository by inject()
    private val firebaseInstance: FirebaseInstance by inject()
    private var currentNavController: LiveData<NavController>? = null
    private var state = 99

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }
        subscribeTopic()
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        super.onRestoreInstanceState(savedInstanceState!!)

        setupBottomNavigationBar()
    }

    private fun setupBottomNavigationBar() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)

        val navGraphIds = listOf(R.navigation.dashboard, R.navigation.event, R.navigation.profile)

        val controller = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_fragment,
            intent = intent
        )

        controller.observe(this, { navController ->
            setupActionBarWithNavController(navController)
        })
        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    private fun subscribeTopic() = Coroutines.main {
        userRepository.getUserData().observe(this, this)
    }

    override fun onChanged(user: User) {
        val groupList = mutableListOf<String>()
        if (user.groupList !== null) {
            groupList.addAll(user.groupList!!)
        }
        Log.d("USER GROUP LIST", user.groupList.toString())
        if (state < groupList.size) Toast.makeText(
            this,
            "Anda telah dimasukkan ke group baru!",
            Toast.LENGTH_LONG
        ).show()
        PreferenceProvider(this).saveUserGroupList(user.uid!!, groupList)
        groupList.forEach { group ->
            firebaseInstance.fcmSubscribeToTopic(group)
                .addOnCompleteListener { task ->
                    var msg = "subscribed to $group"
                    if (!task.isSuccessful) {
                        msg = "failed to subscribe with $group"
                    }
                    Log.d(TAG, msg)
                }
        }
        state = groupList.size
    }
}
