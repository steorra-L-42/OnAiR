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
import com.fm404.onair.core.contract.auth.FCMServiceContract
import com.fm404.onair.data.remote.api.auth.UserApi
import com.fm404.onair.domain.model.auth.FCMTokenRequest
import com.fm404.onair.domain.repository.auth.UserRepository
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

private const val FCM_TYPE_STORY_CHOSEN = "story_chosen"
private const val FCM_TYPE_CHANNEL_CREATED = "channel_created"

private const val TAG = "FCMService"

@Singleton
@AndroidEntryPoint
class FCMService : FirebaseMessagingService(), FCMServiceContract {
    @Inject
//    lateinit var userRepository: UserRepository


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

    override fun getToken(callback: (String) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d(TAG, "FCM Token: $token")
                callback(token)
            } else {
                Log.w(TAG, "Fetching FCM token failed", task.exception)
                callback("")
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
//                sendTokenToServer(token) // 서버에 전송
            } else {
                Log.w(TAG, "Fetching FCM token failed", task.exception)
            }
        }

//        Log.d(TAG, "onCreate: BASE URL = ${fcmApi?.baseUrl()}")

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "onMessageReceived: Received message")

        if (remoteMessage.data.isNotEmpty()) {
            processMessage(remoteMessage.data)
        }
    }

    private fun processMessage(data: Map<String, String>) {
        Log.d(TAG, "processMessage: Data payload: $data")

        when (data["type"]) {
            FCM_TYPE_STORY_CHOSEN -> {
                val storyTitle = data["story_title"] ?: "Unknown Story"
                val channelName = data["channel_name"] ?: "Unknown Channel"

                sendNotification(
                    title = "사연이 채택됐어요!",
                    content = "$channelName 채널에서 $storyTitle 사연이 채택됐어요!"
                )
            }

            FCM_TYPE_CHANNEL_CREATED -> {
                val channelName = data["channel_name"] ?: "Unknown Channel"

                sendNotification(
                    title = "새 채널 생성!",
                    content = "$channelName 채널이 생성되었어요. 앱에서 확인해보세요!"
                )
            }

            else -> Log.d(TAG, "processMessage: Unknown type")
        }

    }

    private fun sendNotification(title: String, content: String) {
        val notificationId = 1001
        val channelId = "onair_default"

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(com.fm404.onair.core.common.R.drawable.ic_onair)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // 알림 클릭 시 열릴 액티비티 지정
//        val intent = Intent(this, MainActivity::class.java) // MainActivity 수정 가능
//        val pendingIntent = PendingIntent.getActivity(
//            this,
//            0,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//        notificationBuilder.setContentIntent(pendingIntent)

        // 알림 표시
        val notificationManager = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(TAG, "Permission for notifications not granted.")
            return
        }
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("Token", "새 토큰: $token")
        // 백엔드 서버에 FCM 토큰 전송
//        sendTokenToServer(token)
    }

//    private fun sendTokenToServer(token: String) {
//        CoroutineScope(Dispatchers.IO).launch {
//            userRepository.registerFCMToken(FCMTokenRequest(fcmToken = token))
//                .onSuccess {
//                    Log.d(TAG, "FCM 토큰 정상 등록됨")
//                }
//                .onFailure { exception ->
//                    Log.d(TAG, "FCM 토큰 등록 실패: ${exception.message}")
//                }
//        }
//    }

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