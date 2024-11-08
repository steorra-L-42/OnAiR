package com.fm404.onair.core.network.di

import com.fm404.onair.BuildConfig
import com.fm404.onair.core.network.interceptor.LoggingInterceptor
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.*
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.*

@Module
@InstallIn(SingletonComponent::class)
object MediaNetworkModule {
    @Provides
    @Singleton
    @Named("mediaLoggingInterceptor")
    fun provideMediaLoggingInterceptor(): LoggingInterceptor {
        return LoggingInterceptor()
    }

    @Provides
    @Singleton
    @Named("mediaOkHttpClient")
    fun provideMediaOkHttpClient(
        @Named("mediaLoggingInterceptor") loggingInterceptor: LoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("mediaRetrofit")
    fun provideMediaRetrofit(
        @Named("mediaOkHttpClient") okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.MEDIA_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}