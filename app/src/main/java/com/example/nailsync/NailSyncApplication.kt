package com.example.nailsync

import android.app.Application
import com.example.nailsync.data.seeder.DataSeeder
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class NailSyncApplication : Application() {

    @Inject lateinit var dataSeeder: DataSeeder

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch { dataSeeder.seed() }
    }
}
