package com.example.kotlin_app_study.bridge

import android.app.Activity
import android.util.Log

/**
 * 原生功能总桥接器。
 * 游戏引擎通过此类统一调用所有原生功能。
 *
 * 支持的方法：
 * - showInterstitial: 展示插屏广告
 * - showRewarded: 展示激励视频
 * - isRewardedReady: 检查激励视频是否就绪
 * - preloadAds: 预加载广告
 * - vibrate: 触发震动（预留）
 * - share: 分享（预留）
 * - openUrl: 打开链接（预留）
 */
object NativeBridge : GameBridge {

    private val tag = "NativeBridge"

    override fun callNative(method: String, params: String, callback: String) {
        Log.d(tag, "callNative: method=$method, params=$params, callback=$callback")
        when (method) {
            "showInterstitial" -> AdBridge.showInterstitial()
            "showRewarded" -> AdBridge.showRewarded(callback.ifEmpty { "onRewardCallback" })
            "isRewardedReady" -> {
                val ready = AdBridge.isRewardedReady()
                if (callback.isNotEmpty()) {
                    callGame(callback, """{"ready":$ready}""")
                }
            }
            "preloadAds" -> AdBridge.preloadAds()
            // 预留更多原生功能
            "vibrate" -> Log.d(tag, "TODO: vibrate")
            "share" -> Log.d(tag, "TODO: share $params")
            "openUrl" -> Log.d(tag, "TODO: openUrl $params")
            else -> Log.w(tag, "Unknown method: $method")
        }
    }

    override fun callGame(callbackName: String, data: String) {
        Log.d(tag, "callGame: $callbackName($data)")
        // TODO: 接入 Cocos 时，通过 CocosJavascriptJavaBridge.evalString() 回调游戏引擎
        // CocosHelper.runOnGameThread {
        //     CocosJavascriptJavaBridge.evalString("window.$callbackName($data)")
        // }
    }
}
