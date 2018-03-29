package com.alexeymerov.unsplashviewer.koin

import android.arch.persistence.room.Room
import android.content.Context
import com.alexeymerov.unsplashviewer.data.database.ApplicationDatabase
import com.alexeymerov.unsplashviewer.data.repository.ImageRepository
import com.alexeymerov.unsplashviewer.data.server.ApiService
import com.alexeymerov.unsplashviewer.data.server.ServerCommunicator
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

val repositoryModule: Module = applicationContext {
    bean { provideSharedPrefs(androidApplication().applicationContext) }
    bean { provideDatabase(androidApplication().applicationContext) }
    bean { provideServerCommunicator() }

    bean { ImageRepository(get(), get()) }
}

private fun provideDatabase(context: Context) = Room.databaseBuilder(context, ApplicationDatabase::class.java, "unsplash-database")
        .build()

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




