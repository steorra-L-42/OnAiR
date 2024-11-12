package com.fm404.onair.core.network.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.fm404.onair.core.network.BuildConfig
import com.fm404.onair.core.network.interceptor.AuthInterceptor
import com.fm404.onair.core.network.interceptor.ErrorHandlingInterceptor
import com.fm404.onair.core.network.interceptor.LoggingInterceptor
import dagger.*
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.*
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import com.fm404.onair.core.network.manager.TokenManager

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val DATASTORE_NAME = "onair_preferences"
    private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideTokenManager(dataStore: DataStore<Preferences>): TokenManager {
        return TokenManager(dataStore)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(): AuthInterceptor {
        return AuthInterceptor()
    }

    @Provides
    @Singleton
    fun provideErrorHandlingInterceptor(): ErrorHandlingInterceptor {
        return ErrorHandlingInterceptor()
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): LoggingInterceptor {
        return LoggingInterceptor()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: LoggingInterceptor,
        errorHandlingInterceptor: ErrorHandlingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(errorHandlingInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}