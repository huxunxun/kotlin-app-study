package com.example.kotlin_app_study.network

import com.example.kotlin_app_study.ads.AdConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit 网络客户端。
 * 使用方式：val api = ApiClient.create(YourApiService::class.java)
 */
object ApiClient {

    private const val CONNECT_TIMEOUT = 15L
    private const val READ_TIMEOUT = 15L

    var baseUrl: String = "https://api.example.com/"
        private set

    fun setBaseUrl(url: String) {
        baseUrl = url
        retrofitInstance = null
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .apply {
                if (AdConfig.isTestMode) {
                    addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                }
            }
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    // TODO: 添加 token 等通用 header
                    // .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    private var retrofitInstance: Retrofit? = null

    private fun getRetrofit(): Retrofit {
        return retrofitInstance ?: Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .also { retrofitInstance = it }
    }

    fun <T> create(service: Class<T>): T = getRetrofit().create(service)
}
