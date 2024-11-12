package com.fm404.onair.core.designsystem.component.audiovisualizer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fm404.onair.core.designsystem.theme.OnairBackground

@Composable
fun AudioVisualizer(amplitudes: FloatArray, modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .width(140.dp)
            .height(40.dp)
            .background(OnairBackground)
//            .background(Color.White)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val lineWidth = 5.dp.toPx()
        val spacingBetweenLines = canvasWidth / amplitudes.size
        val cornerRadius = 3.dp.toPx()

        for (index in amplitudes.indices) {
            val scaledAmplitude = (amplitudes[index] / 100f) * canvasHeight
            val x = index * spacingBetweenLines + spacingBetweenLines / 2 - lineWidth / 2

            drawRoundRect(
//                color = Color.Black,
                color = Color.White,
                topLeft = Offset(x, canvasHeight / 2 - scaledAmplitude / 2),
                size = Size(lineWidth, scaledAmplitude),
                cornerRadius = CornerRadius(cornerRadius, cornerRadius)
            )
        }
    }
}
