package com.fm404.onair.core.firebase

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.CATEGORY_MESSAGE
import androidx.core.app.NotificationCompat.CarExtender
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.graphics.drawable.IconCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.fm404.onair.data.remote.api.auth.UserApi
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

private const val FCM_TYPE_STORY_CHOSEN = "story_chosen"

private const val TAG = "FCMService"
class FCMService : FirebaseMessagingService() {

    private lateinit var mNotificationManager: NotificationManagerCompat

//    private var apiClient: ApiClient? = null
//    private var authManager: AuthManager? = null

//    private val fcmApi: Retrofit? by lazy {
//        apiClient?.fcmApi
//    }
//    private val backendService: BackendService? by lazy {
//        fcmApi?.create(BackendService::class.java)
//    }

    private fun initializeDependencies() {

        mNotificationManager = NotificationManagerCompat.from(applicationContext)


    }

    fun getToken(callback: (String) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d(TAG, "FCM Token: $token")
                callback(token)
            } else {
                Log.w(TAG, "Fetching FCM token failed", task.exception)
                callback("Failed")
            }
        }
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate: FCM onCreate")

        initializeDependencies()

        // FCM 토큰 확인
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d(TAG, "FCM Token: $token")
                sendTokenToServer(token) // 서버에 전송
            } else {
                Log.w(TAG, "Fetching FCM token failed", task.exception)
            }
        }

//        Log.d(TAG, "onCreate: BASE URL = ${fcmApi?.baseUrl()}")

    }

    fun processMessage(remoteMessage: RemoteMessage){
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "페이로드: ${remoteMessage.data}")

//            Log.d(TAG, "processMessage: 안드로이드 오토 화면 켜져있는지 = ${AAFocusManager.isAppInFocus}")

            val responseJsonString = Gson().toJson(remoteMessage.data)

            val fcmData = Gson().fromJson(responseJsonString, FCMData::class.java)

            when (fcmData.type) {

                FCM_TYPE_STORY_CHOSEN -> {
//                    val intent = Intent("com.fm404.onair.CLOSE_MENU")
//                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

                }

                else -> {

                }
            }

        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.data.isNotEmpty()) {
            processMessage(remoteMessage)
        }

        // Data 메세지일 때
        remoteMessage.data.let { data ->
            Log.d("FCM Serv", "Data payload: $data")

            if (data.containsKey("type") && data["type"] == FCM_TYPE_STORY_CHOSEN) {

                val intent = Intent("com.fm404.onair.STORY_CHOSEN")

                val stationId = data["stationId"] ?: -1

                intent.putExtra("station_id", stationId)
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
            }
        }

        // Notification 메세지일 때
        remoteMessage.notification?.let { notification ->
            // 알림 내용 로그
            Log.d("FCM Serv", "Notification Title: ${notification.title}")
            Log.d("FCM Serv", "Notification Body: ${notification.body}")


            val intent = Intent("com.fm404.onair.STORY_CHOSEN")
//            intent.putExtra("station_id", notification.body)
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("Token", "새 토큰: $token")
        // 백엔드 서버에 FCM 토큰 전송
        sendTokenToServer(token)
    }

    private fun sendTokenToServer(token: String) {
//        val call = backendService?.registerToken(token)
//        call?.enqueue(object : Callback<Void> {
//            override fun onResponse(call: Call<Void>, response: Response<Void>) {
//                if (response.isSuccessful) {
//                    Log.d(TAG, "onResponse: FCM 토큰 정상 등록됨")
//                } else {
//                    Log.d(TAG, "onResponse: FCM 토큰 등록 실패")
//                }
//            }
//
//            override fun onFailure(call: Call<Void>, t: Throwable) {
//                Log.d(TAG, "onFailure: FCM 토큰 서버 통신 오류 \n${Log.getStackTraceString(t)}")
//            }
//        })
    }

    fun sendCarNotification(title: String, content: String) {
        // Create a builder for the notification
        val builder = NotificationCompat.Builder(applicationContext, "noti_a")
            .setSmallIcon(com.fm404.onair.core.common.R.drawable.ic_onair) // Notification icon
            .setContentTitle(title) // Notification title
            .setContentText(content) // Notification content
            .setPriority(NotificationCompat.PRIORITY_HIGH) // High priority for visibility on Auto

        val icon = BitmapFactory.decodeResource(
            applicationContext.resources,
            com.fm404.onair.core.common.R.drawable.ic_onair
        )

        // Add Android Auto car extensions
        val notification = builder
            .extend(
                CarExtender()
                    .setColor(Color.YELLOW) // Set notification color
//                    .setContentTitle(title) // Customize title for car display
//                    .setContentText(content) // Customize content for car display
                    .setLargeIcon(icon)
            )
            .build()


        val notificationManager = NotificationManagerCompat.from(applicationContext)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        notificationManager.notify(1, notification)
    }


}