package com.example.kotlin_app_study.ads

/**
 * 广告频率控制器，防止过于频繁展示插屏/开屏广告。
 */
class AdFrequencyController(
    private val minIntervalMs: Long = AdConfig.INTERSTITIAL_MIN_INTERVAL_MS,
    private val firstDelayMs: Long = AdConfig.INTERSTITIAL_FIRST_DELAY_MS
) {
    private var lastShowTime = 0L
    private val startTime = System.currentTimeMillis()

    fun canShow(): Boolean {
        val now = System.currentTimeMillis()
        if (now - startTime < firstDelayMs) return false
        if (lastShowTime == 0L) return true
        return now - lastShowTime >= minIntervalMs
    }

    fun onShown() {
        lastShowTime = System.currentTimeMillis()
    }

    fun reset() {
        lastShowTime = 0L
    }
}
