package com.airmineral.agendakeun.ui.home.create

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.airmineral.agendakeun.R

class CreateEventActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        val navController = Navigation.findNavController(this, R.id.create_event_nav_host)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.groupChooserFragment,
                R.id.groupCreatorFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
    }
}
