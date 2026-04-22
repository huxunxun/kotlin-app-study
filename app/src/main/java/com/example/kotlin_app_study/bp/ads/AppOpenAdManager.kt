package com.example.kotlin_app_study.bp.ads

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd

/**
 * 开屏广告（App Open Ad）封装。
 *
 * 触发时机：
 *  - 应用从后台回前台（onStart）；
 *  - 冷启动后第一次回到前台。
 *
 * 频控：
 *  - 实例缓存最多 4 小时，过期后重新加载；
 *  - 加载失败后 1 分钟内不重试；
 *  - 同一次前台内只展示一次；
 *  - 首次冷启动 [skipFirstStart] 默认跳过（避免和系统 Splash 冲突）。
 *
 * 用法：在 [Application.onCreate] 里：
 * ```
 * AppOpenAdManager.register(this)
 * ```
 */
object AppOpenAdManager : Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {

    private const val TAG = "Ads-AppOpen"
    /** AdMob 官方说明：App Open 实例最多缓存 4 小时 */
    private const val CACHE_TTL_MS = 4L * 60L * 60L * 1000L
    private const val FAIL_BACKOFF_MS = 60_000L

    /** 第一次冷启动是否跳过开屏（true=跳过，避免和系统 Splash 冲突） */
    var skipFirstStart: Boolean = true

    @Volatile private var registered = false
    @Volatile private var enabled = true

    @Volatile private var ad: AppOpenAd? = null
    @Volatile private var loading: Boolean = false
    @Volatile private var loadedAt: Long = 0L
    @Volatile private var lastFailAt: Long = 0L

    @Volatile private var currentActivity: Activity? = null
    @Volatile private var isShowing: Boolean = false
    @Volatile private var isFirstStart: Boolean = true

    fun register(app: Application) {
        if (registered) return
        registered = true
        app.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        preload(app)
    }

    /** 临时禁用（例如用户进入测量页时不想被打断），后续手动 [setEnabled] 恢复 */
    fun setEnabled(value: Boolean) { enabled = value }

    private fun isExpired(): Boolean =
        loadedAt > 0 && (System.currentTimeMillis() - loadedAt) > CACHE_TTL_MS

    fun preload(context: Context, adUnitId: String = AdUnits.APP_OPEN) {
        if (loading) return
        if (ad != null && !isExpired()) return
        if (System.currentTimeMillis() - lastFailAt < FAIL_BACKOFF_MS) return

        loading = true
        AppOpenAd.load(
            context.applicationContext,
            adUnitId,
            AdRequest.Builder().build(),
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(loaded: AppOpenAd) {
                    loading = false
                    ad = loaded
                    loadedAt = System.currentTimeMillis()
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

    private fun showIfReady() {
        val activity = currentActivity ?: return
        if (!enabled || isShowing) return
        val current = ad
        if (current == null || isExpired()) {
            preload(activity)
            return
        }
        current.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                ad = null
                isShowing = false
                preload(activity)
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                ad = null
                isShowing = false
                preload(activity)
            }

            override fun onAdShowedFullScreenContent() {
                isShowing = true
            }
        }
        current.show(activity)
    }

    // ====== ProcessLifecycleOwner ======
    override fun onStart(owner: LifecycleOwner) {
        if (isFirstStart && skipFirstStart) {
            isFirstStart = false
            currentActivity?.applicationContext?.let { preload(it) }
            return
        }
        isFirstStart = false
        showIfReady()
    }

    // ====== ActivityLifecycleCallbacks ======
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {
        if (!isShowing) currentActivity = activity
    }
    override fun onActivityResumed(activity: Activity) {
        if (!isShowing) currentActivity = activity
    }
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivity === activity) currentActivity = null
    }
}
