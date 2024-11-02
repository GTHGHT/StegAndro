package io.github.gthght.stegandro.presentation.introduction

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun StepProgressArc(
    modifier: Modifier = Modifier,
    width: Float,
    height: Float,
    numSteps: Int,
    completedSteps: Int = 1
) {
    val secondary = MaterialTheme.colorScheme.secondary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    Canvas(
        modifier = modifier
            .size(width = width.dp, height = height.dp)
    ) {
        val angleSteps = 360f / numSteps
        for (i in 0 until numSteps) {
            drawArc(
                color = if ((i + 1) % numSteps < completedSteps) primaryContainer else secondary,
                startAngle = i * angleSteps,
                sweepAngle = angleSteps - 8f,
                useCenter = false,
                size = Size(
                    width = this.size.width + 48f,
                    height = this.size.height + 48f
                ),
                style = Stroke(width = 16f, cap = StrokeCap.Butt),
                topLeft = Offset(-24f, -24f)
            )
        }
    }
}