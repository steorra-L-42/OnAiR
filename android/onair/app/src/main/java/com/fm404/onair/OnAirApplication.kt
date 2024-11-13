package com.fm404.onair

import android.app.Application
import android.util.Log
import com.fm404.onair.core.firebase.FCMService
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

private const val TAG = "OnAirApplication"

@HiltAndroidApp
class OnAirApplication : Application(){

    lateinit var fcmService: FCMService

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "[모비페이] onCreate: FCM init")
        FirebaseApp.initializeApp(this)

        fcmService = FCMService()

        fcmService.getToken { token ->
            Log.d(TAG, "이 기기의 FCM 토큰: $token")
        }

    }
}