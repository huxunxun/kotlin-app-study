package com.example.kotlin_app_study.network.model

/**
 * 通用 API 响应包装。
 * 服务端返回格式约定：{ "code": 0, "message": "success", "data": {...} }
 */
data class ApiResponse<T>(
    val code: Int = 0,
    val message: String = "",
    val data: T? = null
) {
    val isSuccess: Boolean get() = code == 0
}

/**
 * App 配置（服务端下发）。
 * 可用于远程控制广告开关、功能开关、版本更新等。
 */
data class AppConfig(
    val adEnabled: Boolean = true,
    val interstitialEnabled: Boolean = true,
    val rewardedEnabled: Boolean = true,
    val bannerEnabled: Boolean = true,
    val forceUpdate: Boolean = false,
    val latestVersion: String = "",
    val updateUrl: String = "",
    val notice: String = ""
)
