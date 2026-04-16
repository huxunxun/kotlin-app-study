package com.example.kotlin_app_study.bridge

/**
 * 游戏引擎桥接接口。
 * 定义游戏引擎（Cocos/Unity 等）与原生层的通信协议。
 */
interface GameBridge {

    /**
     * 游戏引擎调用原生功能
     * @param method 方法名（如 "showRewardedAd", "purchase" 等）
     * @param params JSON 格式参数
     * @param callback 回调函数名（用于原生回调给游戏引擎）
     */
    fun callNative(method: String, params: String = "", callback: String = "")

    /**
     * 原生层回调给游戏引擎
     * @param callbackName 回调函数名
     * @param data JSON 格式数据
     */
    fun callGame(callbackName: String, data: String = "")
}
