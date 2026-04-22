package com.example.kotlin_app_study

import android.app.Application
import com.example.kotlin_app_study.bp.ads.AppOpenAdManager
import com.example.kotlin_app_study.bp.ads.InterstitialAdLoader
import com.example.kotlin_app_study.bp.ads.RewardedAdLoader
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration

/**
 * Application 入口。负责：
 *  1) 初始化 AdMob 并把所有设备标记为测试设备；
 *  2) 注册开屏（App Open）生命周期监听；
 *  3) 预加载插页 / 激励，提高首次展示成功率。
 */
class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(listOf("EMULATOR"))
                .build()
        )
        MobileAds.initialize(this) {
            InterstitialAdLoader.preload(this)
            RewardedAdLoader.preload(this)
            AppOpenAdManager.register(this)
        }
    }
}
