package com.example.kotlin_app_study.bp.ads

/**
 * AdMob 广告单元 ID 集中管理。
 *
 * 当前全部使用 Google 官方公开测试 ad-unit（任何账号都可以无限请求，
 * 不会产生收入也不会被风控）。
 * 上线时把这些常量替换成自己的真实 ad-unit ID 即可，业务侧无需改动。
 *
 * 参考：https://developers.google.com/admob/android/test-ads
 */
object AdUnits {
    /** 横幅 / 自适应横幅（首页底栏 / 详情页） */
    const val BANNER = "ca-app-pub-3940256099942544/6300978111"

    /** 插页（操作完成 / 页面切换兜底） */
    const val INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712"

    /** 激励视频（解锁内容） */
    const val REWARDED = "ca-app-pub-3940256099942544/5224354917"

    /** 开屏（App Open，冷启动 / 热启动） */
    const val APP_OPEN = "ca-app-pub-3940256099942544/9257395921"
}
