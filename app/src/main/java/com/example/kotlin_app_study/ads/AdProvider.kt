package com.example.kotlin_app_study.ads

import android.app.Activity
import android.content.Context
import android.view.ViewGroup

/**
 * 广告提供商抽象接口。
 * 实现此接口可对接不同广告平台（AdMob、穿山甲、优量汇等）。
 */
interface AdProvider {

    val providerName: String

    fun initialize(context: Context, onComplete: () -> Unit = {})

    // Banner
    fun loadBanner(activity: Activity, container: ViewGroup, adUnitId: String)
    fun destroyBanner()

    // Interstitial
    fun loadInterstitial(activity: Activity, adUnitId: String, onLoaded: () -> Unit = {}, onFailed: (String) -> Unit = {})
    fun showInterstitial(activity: Activity, onDismissed: () -> Unit = {})
    fun isInterstitialReady(): Boolean

    // Rewarded Video
    fun loadRewarded(activity: Activity, adUnitId: String, onLoaded: () -> Unit = {}, onFailed: (String) -> Unit = {})
    fun showRewarded(activity: Activity, onRewarded: () -> Unit, onDismissed: () -> Unit = {})
    fun isRewardedReady(): Boolean

    // App Open
    fun loadAppOpen(activity: Activity, adUnitId: String, onLoaded: () -> Unit = {}, onFailed: (String) -> Unit = {})
    fun showAppOpen(activity: Activity, onDismissed: () -> Unit = {})
    fun isAppOpenReady(): Boolean

    fun destroy()
}
