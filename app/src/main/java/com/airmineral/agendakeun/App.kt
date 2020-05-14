package com.airmineral.agendakeun

import android.app.Application
import com.airmineral.agendakeun.data.repositories.EventRepository
import com.airmineral.agendakeun.data.repositories.GroupRepository
import com.airmineral.agendakeun.data.repositories.UserRepository
import com.airmineral.agendakeun.ui.home.HomeViewModel
import com.airmineral.agendakeun.ui.home.create.CreateEventViewModel
import com.airmineral.agendakeun.ui.login.AuthViewModel
import com.airmineral.agendakeun.ui.profile.ProfileViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            androidLogger()
            modules(module {
                single { UserRepository() }
                single { GroupRepository() }
                single { EventRepository() }

                viewModel { ProfileViewModel(get()) }
                viewModel { AuthViewModel(get()) }
                viewModel { CreateEventViewModel(get(), get(), get()) }
                viewModel { HomeViewModel(get()) }
            })
        }
    }
}