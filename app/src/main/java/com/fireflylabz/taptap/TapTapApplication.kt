package com.fireflylabz.taptap

import android.app.Application

class TapTapApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: TapTapApplication
            private set
    }
}
