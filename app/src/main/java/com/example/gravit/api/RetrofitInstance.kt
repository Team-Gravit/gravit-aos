package com.inuappcenter.gravit.api

import android.content.Context
import android.util.Log
import com.inuappcenter.gravit.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private val logger: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor { msg ->
            Log.d("HTTP", msg)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val tokenProvider: TokenProvider by lazy {
        AuthPrefsTokenProvider(appContext)
    }

    private val refreshApi: ApiService by lazy {
        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()

        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    val api: ApiService by lazy {
        if (!::appContext.isInitialized) {
            throw IllegalStateException("RetrofitInstance.init(context)를 먼저 호출해야 합니다.")
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .authenticator(
                TokenAuthenticator(
                    context = appContext,
                    tokenProvider = tokenProvider,
                    refreshApi = refreshApi
                )
            )
            .build()

        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}