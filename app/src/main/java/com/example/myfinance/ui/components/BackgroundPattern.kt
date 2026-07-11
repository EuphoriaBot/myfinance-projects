package com.example.myfinance.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

@Composable
fun BackgroundPattern(
    modifier: Modifier = Modifier,
    color: Color = Color.White.copy(alpha = 0.06f)
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val dotSpacing = 28f
        val dotRadius = 1.2f

        val cols = (size.width / dotSpacing).toInt() + 2
        val rows = (size.height / dotSpacing).toInt() + 2

        for (col in 0..cols) {
            for (row in 0..rows) {
                val x = col * dotSpacing
                val y = row * dotSpacing

                val distFromCenter = kotlin.math.sqrt(
                    ((x - size.width / 2) / size.width).toDouble().let { it * it } +
                            ((y - size.height / 2) / size.height).toDouble().let { it * it }
                ).toFloat()

                val alpha = (0.08f - distFromCenter * 0.06f).coerceIn(0.02f, 0.08f)

                drawCircle(
                    color = color.copy(alpha = alpha),
                    radius = dotRadius,
                    center = Offset(x, y)
                )
            }
        }
    }
}