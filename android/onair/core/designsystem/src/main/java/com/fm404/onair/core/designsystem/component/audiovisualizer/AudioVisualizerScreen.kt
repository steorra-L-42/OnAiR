package com.fm404.onair.core.designsystem.component.audiovisualizer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AudioVisualizerScreen(amplitudes: FloatArray, modifier: Modifier = Modifier) {
    val minAmplitude = 5f
    val maxAmplitude = 100f

    val animatedAmplitudes = amplitudes.map { amplitude ->
        animateFloatAsState(
            targetValue = amplitude.coerceIn(minAmplitude, maxAmplitude),
            animationSpec = tween(durationMillis = 100)
        ).value
    }

    AudioVisualizer(amplitudes = animatedAmplitudes.toFloatArray(), modifier = modifier)
}
