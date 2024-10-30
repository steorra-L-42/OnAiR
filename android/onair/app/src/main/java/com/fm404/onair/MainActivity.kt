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
import com.fm404.onair.core.designsystem.theme.OnAirTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.absoluteValue
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

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
                        updateAmplitudes(fft)  // Process FFT data instead
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

        val targetMaxAmplitude = 100f  // Maximum height for the loudest sound
        val compressionFactor = 0.5f   // Adjust between 0 (no compression) and 1 (full compression)

        for (i in 0 until size) {
            val rawAmplitude = newAmplitudes[i].toFloat().absoluteValue

            // Apply dynamic range compression
            val compressedAmplitude = rawAmplitude.pow(compressionFactor)

            // Normalize the compressed amplitude
            val normalizedAmplitude = (compressedAmplitude / 127f.pow(compressionFactor)) * targetMaxAmplitude

            // Ensure a minimum height
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
    }
}
