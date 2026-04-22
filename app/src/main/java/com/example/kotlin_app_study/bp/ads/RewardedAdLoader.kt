package com.example.kotlin_app_study.bp.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

/**
 * 激励视频（Rewarded）封装。
 *
 * 业务用法：
 * ```
 * RewardedAdLoader.show(activity, onReward = { /* 给奖励 */ }, onClosed = { /* 收尾 */ })
 * ```
 *
 * - 如果实例未就绪，会立即触发预加载，并直接回调 [onClosed]，
 *   业务流程不会被阻塞，但本次也不会发奖（[onReward] 不会被调用）。
 * - 展示完成（无论是否被奖励）后自动 preload 下一次实例。
 */
object RewardedAdLoader {
    private const val TAG = "Ads-Rewarded"
    private const val FAIL_BACKOFF_MS = 60_000L

    @Volatile private var ad: RewardedAd? = null
    @Volatile private var loading: Boolean = false
    @Volatile private var lastFailAt: Long = 0L

    fun preload(context: Context, adUnitId: String = AdUnits.REWARDED) {
        if (ad != null || loading) return
        if (System.currentTimeMillis() - lastFailAt < FAIL_BACKOFF_MS) return

        loading = true
        RewardedAd.load(
            context.applicationContext,
            adUnitId,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(loaded: RewardedAd) {
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

    fun isReady(): Boolean = ad != null

    fun show(
        activity: Activity,
        onReward: () -> Unit,
        onClosed: () -> Unit = {}
    ) {
        val current = ad
        if (current == null) {
            preload(activity)
            onClosed()
            return
        }
        var rewarded = false
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
        current.show(activity) {
            if (!rewarded) {
                rewarded = true
                onReward()
            }
        }
    }
}
