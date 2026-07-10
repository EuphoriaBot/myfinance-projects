package com.example.myfinance.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun BackgroundPattern(
    modifier: Modifier = Modifier,
    color: Color = Color.White.copy(alpha = 0.03f)
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val spacing = 80f
        val cols = (size.width / spacing).toInt() + 2
        val rows = (size.height / spacing).toInt() + 2

        for (col in 0..cols) {
            for (row in 0..rows) {
                val x = col * spacing
                val y = row * spacing
                val patternIndex = (col + row) % 6

                when (patternIndex) {
                    0 -> drawCirclePattern(x, y, color)
                    1 -> drawDiamondPattern(x, y, color)
                    2 -> drawPlusPattern(x, y, color)
                    3 -> drawDotPattern(x, y, color)
                    4 -> drawTrianglePattern(x, y, color)
                    5 -> drawSquarePattern(x, y, color)
                }
            }
        }
    }
}

private fun DrawScope.drawCirclePattern(x: Float, y: Float, color: Color) {
    drawCircle(
        color = color,
        radius = 6f,
        center = Offset(x, y),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
    )
}

private fun DrawScope.drawDiamondPattern(x: Float, y: Float, color: Color) {
    val path = androidx.compose.ui.graphics.Path().apply {
        moveTo(x, y - 7f)
        lineTo(x + 5f, y)
        lineTo(x, y + 7f)
        lineTo(x - 5f, y)
        close()
    }
    drawPath(path, color = color, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f))
}

private fun DrawScope.drawPlusPattern(x: Float, y: Float, color: Color) {
    drawLine(color, Offset(x - 6f, y), Offset(x + 6f, y), strokeWidth = 1.5f)
    drawLine(color, Offset(x, y - 6f), Offset(x, y + 6f), strokeWidth = 1.5f)
}

private fun DrawScope.drawDotPattern(x: Float, y: Float, color: Color) {
    drawCircle(color = color, radius = 2.5f, center = Offset(x, y))
}

private fun DrawScope.drawTrianglePattern(x: Float, y: Float, color: Color) {
    val path = androidx.compose.ui.graphics.Path().apply {
        moveTo(x, y - 7f)
        lineTo(x + 6f, y + 5f)
        lineTo(x - 6f, y + 5f)
        close()
    }
    drawPath(path, color = color, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f))
}

private fun DrawScope.drawSquarePattern(x: Float, y: Float, color: Color) {
    drawRect(
        color = color,
        topLeft = Offset(x - 5f, y - 5f),
        size = androidx.compose.ui.geometry.Size(10f, 10f),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
    )
}