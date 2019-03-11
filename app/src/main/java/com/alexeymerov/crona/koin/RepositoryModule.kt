package com.alexeymerov.crona.koin

import android.content.Context
import com.alexeymerov.crona.data.repository.ImageRepository
import com.alexeymerov.crona.data.server.ApiService
import com.alexeymerov.crona.data.server.ServerCommunicator
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

val repositoryModule = module {
    single { provideSharedPrefs(androidContext()) }
    single { provideServerCommunicator() }

    single { ImageRepository(get()) }
}

private fun provideSharedPrefs(context: Context) = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

private const val API_URL = "https://api.unsplash.com/"
private fun provideServerCommunicator(): ServerCommunicator {
    val okHttpClientBuilder = OkHttpClient.Builder()
            .connectionPool(ConnectionPool(5, 30, TimeUnit.SECONDS))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)

//    if (BuildConfig.DEBUG) {
//        val httpLoggingInterceptor = HttpLoggingInterceptor()
//        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
//        okHttpClientBuilder.addInterceptor(httpLoggingInterceptor)
//                .addNetworkInterceptor(StethoInterceptor())
//    }

    val retrofitBuilder = Retrofit.Builder()
            .client(okHttpClientBuilder.build())
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

    val retrofit = retrofitBuilder.baseUrl(API_URL).build()
    val apiService = retrofit.create<ApiService>(ApiService::class.java)
    return ServerCommunicator(apiService)
}




