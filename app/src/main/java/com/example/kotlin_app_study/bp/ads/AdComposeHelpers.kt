package com.example.kotlin_app_study.bp.ads

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/** Compose 内拿到当前承载的 Activity（找不到返回 null）。*/
@Composable
fun rememberActivity(): Activity? {
    val ctx = LocalContext.current
    return ctx.findActivity()
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

/**
 * 业务侧"展示插页 / 否则直接跳过"的语法糖。
 * 用法：
 * ```
 * val act = rememberActivity()
 * showInterstitial(act) { onSaved() }
 * ```
 */
fun showInterstitial(activity: Activity?, onClosed: () -> Unit) {
    if (activity == null) {
        onClosed()
    } else {
        InterstitialAdLoader.show(activity, onClosed)
    }
}

fun showRewarded(activity: Activity?, onReward: () -> Unit, onClosed: () -> Unit = {}) {
    if (activity == null) {
        onClosed()
    } else {
        RewardedAdLoader.show(activity, onReward, onClosed)
    }
}
