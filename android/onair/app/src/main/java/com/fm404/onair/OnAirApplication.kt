package com.fm404.onair

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.AudioAttributes
import android.media.RingtoneManager
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

        Log.d(TAG, "[OnAiR] onCreate: FCM init")
        val firebaseApp = FirebaseApp.initializeApp(this)
//        FirebaseApp.initializeApp(this)

        fcmService = FCMService()

        fcmService.getToken { token ->
            Log.d(TAG, "이 기기의 FCM 토큰: $token")
        }
        createNotificationChannel()
        firebaseApp?.setDataCollectionDefaultEnabled(false)



    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "onair_default",
            "OnAiR 기본 알림채널",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "OnAiR 알림을 처리하기 위한 채널이에요."
            enableVibration(true)
            setSound(
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()
            )
        }

        val notificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}