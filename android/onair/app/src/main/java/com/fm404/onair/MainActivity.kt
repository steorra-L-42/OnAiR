package com.fm404.onair

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.audiofx.Visualizer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.fm404.onair.core.designsystem.component.audiovisualizer.AudioVisualizerScreen
import androidx.navigation.compose.rememberNavController
import com.fm404.onair.core.designsystem.theme.OnAirTheme
import com.fm404.onair.core.navigation.component.BottomNavBar
import com.fm404.onair.core.navigation.graph.MainNavGraph
import com.fm404.onair.core.navigation.model.NavRoute
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.pow

const val AV_LINES = 10

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var visualizer: Visualizer
    private val amplitudesState = mutableStateOf(FloatArray(AV_LINES))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
        }

        mediaPlayer = MediaPlayer.create(this, R.raw.eta)
        mediaPlayer.isLooping = true

        setContent {
            OnAirTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 60.dp, horizontal = 100.dp)
                ) { innerPadding ->
                    AudioVisualizerScreen(
                        amplitudes = amplitudesState.value,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                MainScreen()
            }
        }

        mediaPlayer.start()
        setupVisualizer()
    }

    private fun setupVisualizer() {
        try {
            visualizer = Visualizer(mediaPlayer.audioSessionId).apply {
                captureSize = Visualizer.getCaptureSizeRange()[1]
                setDataCaptureListener(object : Visualizer.OnDataCaptureListener {
                    override fun onWaveFormDataCapture(
                        visualizer: Visualizer,
                        waveform: ByteArray,
                        samplingRate: Int
                    ) {
                        // Handle waveform data if needed
                    }

                    override fun onFftDataCapture(
                        visualizer: Visualizer,
                        fft: ByteArray,
                        samplingRate: Int
                    ) {
                        updateAmplitudes(fft)
                    }
                }, Visualizer.getMaxCaptureRate(), false, true)
                enabled = true
            }
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }
    }


    private fun updateAmplitudes(newAmplitudes: ByteArray) {
        val size = minOf(newAmplitudes.size, amplitudesState.value.size)
        val amplitudes = FloatArray(amplitudesState.value.size)

        val targetMaxAmplitude = 100f  // 제일 높은 소리에 대한 최대 크기
        val compressionFactor = 0.5f // 압축률 0 ~ 1 사이 조정

        for (i in 0 until size) {
            val rawAmplitude = newAmplitudes[i].toFloat().absoluteValue

            val compressedAmplitude = rawAmplitude.pow(compressionFactor)

            val normalizedAmplitude = (compressedAmplitude / 127f.pow(compressionFactor)) * targetMaxAmplitude

            // 최소 높이
            amplitudes[i] = max(5f, normalizedAmplitude)
        }

        runOnUiThread {
            amplitudesState.value = amplitudes
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        if (::visualizer.isInitialized) {
            visualizer.release()
        }
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val startDestination = remember { NavRoute.Home.route }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            BottomNavBar(
                navController = navController
            )
        }
    ) { paddingValues ->
        MainNavGraph(
            navController = navController,
            startDestination = startDestination
        )
    }
}