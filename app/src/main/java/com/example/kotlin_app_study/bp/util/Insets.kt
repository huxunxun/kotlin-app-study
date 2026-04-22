package com.example.kotlin_app_study.bp.util

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

/**
 * 系统状态栏（信号栏 / 时钟栏）真实高度。
 *
 * 数据源是 `WindowInsets.statusBars`，由系统在 edge-to-edge 模式下
 * 动态填入，已经包含：
 *   - 状态栏图标条；
 *   - 刘海 / 居中挖孔 / 灵动岛凹槽；
 *   - 横竖屏切换。
 *
 * 用法：
 * ```
 * val topPad = currentStatusBarHeight()
 * Modifier.padding(top = topPad)
 * ```
 *
 * 与 `Modifier.statusBarsPadding()` 完全等价；只是把"高度"以 Dp 暴露出来，
 * 方便在不依赖 padding 修饰符的位置使用（如绝对偏移、自定义 Layout）。
 */
@Composable
fun currentStatusBarHeight(): Dp {
    val density = LocalDensity.current
    val insets = WindowInsets.statusBars
    return with(density) { insets.getTop(density).toDp() }
}

/** 同上但把值缓存起来，订阅其变化（旋转 / 折叠屏切态时刷新）。*/
@Composable
fun rememberStatusBarHeight(): Dp {
    val density = LocalDensity.current
    val insets = WindowInsets.statusBars
    val topPx = insets.getTop(density)
    return remember(density, topPx) { with(density) { topPx.toDp() } }
}
