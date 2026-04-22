package com.example.kotlin_app_study.bp.theme

import androidx.compose.ui.graphics.Color

/**
 * 调色板严格按 blood_pressure_app/screenshots 目视比色调出来：
 *  - 全局深色主题：背景 #1A1F36（深 navy）+ 卡面 #2B304A（亮一档 navy）
 *  - 主色蓝：#4D7CFF（按钮 / 选中态 / 主页心率按钮）
 *  - 正常绿：#3CCB6E（柱状图、状态点、Add 高亮行）
 *  - 等级色（低 → 危象）：蓝 / 绿 / 黄 / 橙 / 红
 *  - 资讯横幅 6 色循环：teal / yellow / red / green / blue / coral
 */
object BPColors {
    val Primary = Color(0xFF4D7CFF)
    val PrimaryDark = Color(0xFF3565F0)
    val PrimaryContainer = Color(0xFF2A3A66)

    val Accent = Color(0xFF3CCB6E)
    val AccentDark = Color(0xFF1FA855)

    val Warning = Color(0xFFFFC629)
    val Danger = Color(0xFFFF5050)
    val DangerDark = Color(0xFFB31010)

    // 5 级配色
    val LevelLow = Color(0xFF3D8BFD)
    val LevelNormal = Color(0xFF3CCB6E)
    val LevelElevated = Color(0xFFFFC629)
    val LevelStage1 = Color(0xFFFF8A00)
    val LevelStage2 = Color(0xFFFF5050)
    val LevelCrisis = Color(0xFFB31010)

    // 全局背景：深 navy，截图所示
    val Background = Color(0xFF1A1F36)
    val Surface = Color(0xFF2B304A)        // 卡片背景
    val SurfaceVariant = Color(0xFF353B5A) // 输入 / 横幅底
    val OnSurface = Color(0xFFF2F2F5)
    val OnSurfaceVariant = Color(0xFFB7BACB)
    val Divider = Color(0xFF3A4163)

    // 资讯横幅 6 色（循环）
    val BannerTeal = Color(0xFF1FAFA0)
    val BannerYellow = Color(0xFFF7C53B)
    val BannerRed = Color(0xFFCF4F5E)
    val BannerGreen = Color(0xFF44A36C)
    val BannerBlue = Color(0xFF6E91E4)
    val BannerCoral = Color(0xFFD46B72)
}
