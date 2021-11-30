package com.airmineral.agendakeun

import android.app.Application
import com.airmineral.agendakeun.data.FirebaseInstance
import com.airmineral.agendakeun.data.repositories.EventRepository
import com.airmineral.agendakeun.data.repositories.GroupRepository
import com.airmineral.agendakeun.data.repositories.UserRepository
import com.airmineral.agendakeun.ui.dashboard.DashboardViewModel
import com.airmineral.agendakeun.ui.event.HomeViewModel
import com.airmineral.agendakeun.ui.event.create.CreateEventViewModel
import com.airmineral.agendakeun.ui.login.AuthViewModel
import com.airmineral.agendakeun.ui.profile.GroupEditorViewModel
import com.airmineral.agendakeun.ui.profile.ProfileViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            androidLogger(Level.ERROR)

            val koinModules = module {
                single { FirebaseInstance() }
                single { UserRepository(get()) }
                single { GroupRepository(get()) }
                single { EventRepository(get(), get()) }

                viewModel { ProfileViewModel(get(), get()) }
                viewModel { AuthViewModel(get()) }
                viewModel { CreateEventViewModel(get(), get(), get()) }
                viewModel { HomeViewModel(get()) }
                viewModel { DashboardViewModel(get(), get()) }
                viewModel { GroupEditorViewModel(get(), get()) }
            }

            koin.loadModules(listOf(koinModules))
            koin.createRootScope()
        }
    }
}