package io.github.gthght.stegandro.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun BeveledIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val gradient = listOf(
        MaterialTheme.colorScheme.surfaceContainerHighest,
        MaterialTheme.colorScheme.surfaceContainerLow
    )
    val isDarkMode = isSystemInDarkTheme()
    Box(
        modifier
            .size(56.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isDarkMode) gradient else gradient.reversed()
                ),
                shape = CircleShape
            )
            .border(
                width = 4.dp,
                shape = CircleShape,
                brush = Brush.verticalGradient(
                    colors = if (isDarkMode) gradient.reversed() else gradient
                )
            ), contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = Modifier.size(30.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BeveledIconPreview() {
    BeveledIcon(imageVector = Icons.Default.Person, contentDescription = "Person")
}