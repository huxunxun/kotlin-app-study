package com.example.kotlin_app_study

import android.app.Application
import android.util.Log
import com.example.kotlin_app_study.ads.AdManager

class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        initAdSdk()
    }

    private fun initAdSdk() {
        AdManager.init(this) {
            Log.d("App", "Ad SDK initialized")
        }
    }
}
