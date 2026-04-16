package com.example.kotlin_app_study.ads

object AdConfig {

    var isTestMode: Boolean = true

    // AdMob 测试广告 ID（Google 官方提供的测试 ID，开发时使用）
    object TestIds {
        const val ADMOB_APP_ID = "ca-app-pub-3940256099942544~3347511713"
        const val BANNER = "ca-app-pub-3940256099942544/6300978111"
        const val INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712"
        const val REWARDED = "ca-app-pub-3940256099942544/5224354917"
        const val APP_OPEN = "ca-app-pub-3940256099942544/9257395921"
    }

    // 正式广告 ID（上架前替换为真实 ID）
    object ProdIds {
        const val ADMOB_APP_ID = "YOUR_ADMOB_APP_ID"
        const val BANNER = "YOUR_BANNER_ID"
        const val INTERSTITIAL = "YOUR_INTERSTITIAL_ID"
        const val REWARDED = "YOUR_REWARDED_ID"
        const val APP_OPEN = "YOUR_APP_OPEN_ID"
    }

    fun getBannerId(): String = if (isTestMode) TestIds.BANNER else ProdIds.BANNER
    fun getInterstitialId(): String = if (isTestMode) TestIds.INTERSTITIAL else ProdIds.INTERSTITIAL
    fun getRewardedId(): String = if (isTestMode) TestIds.REWARDED else ProdIds.REWARDED
    fun getAppOpenId(): String = if (isTestMode) TestIds.APP_OPEN else ProdIds.APP_OPEN

    // 插屏广告最小间隔（毫秒）
    const val INTERSTITIAL_MIN_INTERVAL_MS = 60_000L
    // 插屏广告首次延迟（App 启动后多久才开始展示，毫秒）
    const val INTERSTITIAL_FIRST_DELAY_MS = 30_000L
}
