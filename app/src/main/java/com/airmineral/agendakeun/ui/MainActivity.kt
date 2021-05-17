package com.airmineral.agendakeun.ui

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.airmineral.agendakeun.R
import com.airmineral.agendakeun.data.FirebaseInstance
import com.airmineral.agendakeun.data.preferences.PreferenceProvider
import com.airmineral.agendakeun.data.repositories.GroupRepository
import com.airmineral.agendakeun.data.repositories.UserRepository
import com.airmineral.agendakeun.util.Coroutines
import com.airmineral.agendakeun.util.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.android.ext.android.inject

class MainActivity() : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    private val groupRepository: GroupRepository by inject()
    private val userRepository: UserRepository by inject()
    private val firebaseInstance: FirebaseInstance by inject()
    private var currentNavController: LiveData<NavController>? = null

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
        userRepository.getUserData().observe(this, { user ->
            PreferenceProvider(this).saveUserGroupList(user.uid!!, user.groupList!!)
            user.groupList!!.forEach { group ->
                firebaseInstance.fcmSubscribeToTopic(group)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "subscribed to $group")
                        }
                    }
            }
        })
    }
}
