package com.fm404.onair

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AudioVisualizerScreen(amplitudes: FloatArray, modifier: Modifier = Modifier) {
    AudioVisualizer(amplitudes = amplitudes, modifier = modifier)
}
