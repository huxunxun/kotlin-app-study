package com.example.kotlin_app_study.bridge

import android.app.Activity
import android.util.Log
import com.example.kotlin_app_study.ads.AdManager

/**
 * 广告桥接层 —— 供游戏引擎调用广告功能。
 *
 * Cocos Creator 调用示例（TypeScript）：
 * ```typescript
 * // 展示插屏广告
 * native.reflection.callStaticMethod(
 *     "com/example/kotlin_app_study/bridge/AdBridge",
 *     "showInterstitial",
 *     "()V"
 * );
 *
 * // 展示激励视频
 * native.reflection.callStaticMethod(
 *     "com/example/kotlin_app_study/bridge/AdBridge",
 *     "showRewarded",
 *     "(Ljava/lang/String;)V",
 *     "onRewardCallback"
 * );
 * ```
 */
object AdBridge {

    private val tag = "AdBridge"
    private var activityRef: Activity? = null
    private var gameBridge: GameBridge? = null

    fun init(activity: Activity, bridge: GameBridge? = null) {
        activityRef = activity
        gameBridge = bridge
    }

    @JvmStatic
    fun showInterstitial() {
        val activity = activityRef ?: return
        activity.runOnUiThread {
            AdManager.showInterstitial(activity) {
                Log.d(tag, "Interstitial dismissed")
                gameBridge?.callGame("onInterstitialDismissed")
            }
        }
    }

    @JvmStatic
    fun showRewarded(callbackName: String = "onRewardCallback") {
        val activity = activityRef ?: return
        activity.runOnUiThread {
            AdManager.showRewarded(
                activity,
                onRewarded = {
                    Log.d(tag, "User earned reward")
                    gameBridge?.callGame(callbackName, """{"rewarded":true}""")
                },
                onDismissed = {
                    Log.d(tag, "Rewarded dismissed")
                }
            )
        }
    }

    @JvmStatic
    fun isRewardedReady(): Boolean = AdManager.isRewardedReady()

    @JvmStatic
    fun preloadAds() {
        val activity = activityRef ?: return
        AdManager.preloadInterstitial(activity)
        AdManager.preloadRewarded(activity)
    }

    fun destroy() {
        activityRef = null
        gameBridge = null
    }
}
