package com.example.kotlin_app_study.network.api

import com.example.kotlin_app_study.network.model.ApiResponse
import com.example.kotlin_app_study.network.model.AppConfig
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 配置接口 —— 获取服务端下发的 App 配置。
 * 可用于远程控制广告开关、强制更新、公告等。
 */
interface ConfigApi {

    @GET("config")
    suspend fun getAppConfig(
        @Query("app_id") appId: String,
        @Query("version") version: String,
        @Query("platform") platform: String = "android"
    ): ApiResponse<AppConfig>
}
