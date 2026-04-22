package com.example.kotlin_app_study.bp.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/**
 * 插页广告（Interstitial）封装。
 *
 * 设计目标：业务方只调一行 `InterstitialAdLoader.show(activity) { /* 关闭后回调 */ }`，
 * 内部维持以下状态：
 *   - 应用启动时就 `preload()` 预加载，提高首次展示成功率；
 *   - `show` 时若实例就绪，立即展示；展示完成后立即再 preload 一份给下次用；
 *   - `show` 时若实例未就绪，直接走"关闭回调"，绝不阻塞业务；
 *   - 加载失败 1 分钟内不会再重试，避免暴风请求。
 */
object InterstitialAdLoader {
    private const val TAG = "Ads-Interstitial"
    /** 同一加载失败后最少等待多少毫秒才再次发起请求 */
    private const val FAIL_BACKOFF_MS = 60_000L

    @Volatile private var ad: InterstitialAd? = null
    @Volatile private var loading: Boolean = false
    @Volatile private var lastFailAt: Long = 0L

    /** 应用启动 / 上次展示完成后调用，预加载下一次的实例 */
    fun preload(context: Context, adUnitId: String = AdUnits.INTERSTITIAL) {
        if (ad != null || loading) return
        if (System.currentTimeMillis() - lastFailAt < FAIL_BACKOFF_MS) return

        loading = true
        InterstitialAd.load(
            context.applicationContext,
            adUnitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(loaded: InterstitialAd) {
                    loading = false
                    ad = loaded
                    Log.d(TAG, "loaded")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    loading = false
                    ad = null
                    lastFailAt = System.currentTimeMillis()
                    Log.w(TAG, "load failed: ${error.code} ${error.message}")
                }
            }
        )
    }

    /**
     * 展示插页。如果未就绪则直接回调 [onClosed]，业务流程不会被阻塞。
     * 展示完成后会自动 preload 下一次的实例。
     */
    fun show(activity: Activity, onClosed: () -> Unit = {}) {
        val current = ad
        if (current == null) {
            preload(activity)
            onClosed()
            return
        }
        current.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                ad = null
                preload(activity)
                onClosed()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                ad = null
                preload(activity)
                onClosed()
            }
        }
        current.show(activity)
    }
}
