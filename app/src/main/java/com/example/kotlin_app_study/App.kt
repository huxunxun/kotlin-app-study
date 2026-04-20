package com.example.kotlin_app_study

import android.app.Application
import android.util.Log
import com.example.kotlin_app_study.ads.AdManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        instance = this

        // 广告 SDK 延迟异步初始化，不阻塞启动
        appScope.launch {
            AdManager.init(this@App) {
                Log.d("App", "Ad SDK initialized")
            }
        }
    }
}
