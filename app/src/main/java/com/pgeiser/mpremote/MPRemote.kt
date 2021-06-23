package com.pgeiser.mpremote

import android.app.Application
import timber.log.Timber

class MPRemote : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}