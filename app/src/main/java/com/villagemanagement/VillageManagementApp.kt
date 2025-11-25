package com.villagemanagement

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VillageManagementApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
