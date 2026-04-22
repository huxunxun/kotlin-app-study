package com.example.kotlin_app_study.bp.ads

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

/**
 * 自适应横幅广告（Anchored Adaptive Banner）。
 *
 * 用法：
 * ```
 * AdBanner(modifier = Modifier.fillMaxWidth())
 * ```
 *
 * 默认使用 [AdUnits.BANNER]，可通过 [adUnitId] 覆盖。
 * 高度按当前屏宽自动算（一般 50~60dp）。
 */
@Composable
fun AdBanner(
    modifier: Modifier = Modifier,
    adUnitId: String = AdUnits.BANNER,
) {
    val context = LocalContext.current
    val widthDp = context.resources.configuration.screenWidthDp
    val adSize: AdSize = remember(widthDp) {
        AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, widthDp)
    }
    val heightDp: Dp = remember(adSize) { adSize.height.dp }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(heightDp),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { ctx ->
                AdView(ctx).apply {
                    setAdSize(adSize)
                    setAdUnitId(adUnitId)
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}
