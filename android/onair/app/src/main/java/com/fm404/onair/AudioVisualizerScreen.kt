package com.fm404.onair

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AudioVisualizerScreen(amplitudes: FloatArray, modifier: Modifier = Modifier) {
    val minAmplitude = 5f
    val maxAmplitude = 1000f

    val animatedAmplitudes = amplitudes.map { amplitude ->
        animateFloatAsState(
            targetValue = amplitude.coerceIn(minAmplitude, maxAmplitude),
            animationSpec = tween(durationMillis = 100)
        ).value
    }

    AudioVisualizer(amplitudes = animatedAmplitudes.toFloatArray(), modifier = modifier)
}
