package com.example.kotlin_app_study.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.ViewGroup

/**
 * 广告统一管理器。
 * 对外提供统一的广告调用接口，内部可切换不同 AdProvider。
 * 使用方式：AdManager.init(context) → AdManager.showInterstitial(activity) ...
 */
object AdManager {

    private val tag = "AdManager"
    private var provider: AdProvider = AdMobProvider()
    private val frequencyController = AdFrequencyController()
    private var initialized = false
    private var isAdFree = false

    /**
     * 切换广告提供商（用于未来扩展穿山甲、优量汇等）
     */
    fun setProvider(adProvider: AdProvider) {
        provider.destroy()
        provider = adProvider
        initialized = false
    }

    fun getProviderName(): String = provider.providerName

    /**
     * 设置为去广告模式（用户内购后调用）
     */
    fun setAdFree(adFree: Boolean) {
        isAdFree = adFree
        if (adFree) {
            provider.destroyBanner()
        }
    }

    fun isAdFree(): Boolean = isAdFree

    // ============ 初始化 ============

    fun init(context: Context, onComplete: () -> Unit = {}) {
        if (initialized) {
            onComplete()
            return
        }
        provider.initialize(context) {
            initialized = true
            Log.d(tag, "AdManager initialized with ${provider.providerName}")
            onComplete()
        }
    }

    // ============ Banner ============

    fun loadBanner(activity: Activity, container: ViewGroup) {
        if (isAdFree) return
        provider.loadBanner(activity, container, AdConfig.getBannerId())
    }

    fun destroyBanner() {
        provider.destroyBanner()
    }

    // ============ Interstitial ============

    fun preloadInterstitial(activity: Activity) {
        if (isAdFree) return
        if (!provider.isInterstitialReady()) {
            provider.loadInterstitial(activity, AdConfig.getInterstitialId())
        }
    }

    fun showInterstitial(activity: Activity, force: Boolean = false, onDismissed: () -> Unit = {}) {
        if (isAdFree) {
            onDismissed()
            return
        }
        if (!force && !frequencyController.canShow()) {
            Log.d(tag, "Interstitial skipped: frequency limit")
            onDismissed()
            return
        }
        if (!provider.isInterstitialReady()) {
            Log.d(tag, "Interstitial not ready")
            preloadInterstitial(activity)
            onDismissed()
            return
        }
        frequencyController.onShown()
        provider.showInterstitial(activity) {
            preloadInterstitial(activity)
            onDismissed()
        }
    }

    // ============ Rewarded ============

    fun preloadRewarded(activity: Activity) {
        if (!provider.isRewardedReady()) {
            provider.loadRewarded(activity, AdConfig.getRewardedId())
        }
    }

    fun isRewardedReady(): Boolean = provider.isRewardedReady()

    fun showRewarded(activity: Activity, onRewarded: () -> Unit, onDismissed: () -> Unit = {}) {
        if (!provider.isRewardedReady()) {
            Log.d(tag, "Rewarded not ready")
            preloadRewarded(activity)
            onDismissed()
            return
        }
        provider.showRewarded(activity, onRewarded) {
            preloadRewarded(activity)
            onDismissed()
        }
    }

    // ============ App Open ============

    fun preloadAppOpen(activity: Activity) {
        if (isAdFree) return
        if (!provider.isAppOpenReady()) {
            provider.loadAppOpen(activity, AdConfig.getAppOpenId())
        }
    }

    fun showAppOpen(activity: Activity, onDismissed: () -> Unit = {}) {
        if (isAdFree) {
            onDismissed()
            return
        }
        if (!provider.isAppOpenReady()) {
            onDismissed()
            return
        }
        provider.showAppOpen(activity, onDismissed)
    }

    fun destroy() {
        provider.destroy()
        initialized = false
    }
}
