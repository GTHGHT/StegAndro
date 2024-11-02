package io.github.gthght.stegandro.presentation.introduction

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp


@Composable
fun ImageLastIntroContent(modifier: Modifier = Modifier, content: IntroContent) {
    Column(modifier = modifier) {
        Text(
            content.title,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            content.description,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.size(32.dp))
        Box {
            Image(
                painter = content.introImage,
                contentDescription = "Introduction Screen One",
                colorFilter = ColorFilter.tint(
                    MaterialTheme.colorScheme.primary,
                    blendMode = BlendMode.Hue
                )
            )
            Image(
                painter = content.introImage,
                contentDescription = "Introduction Screen One",
                colorFilter = ColorFilter.tint(
                    MaterialTheme.colorScheme.surface,
                    blendMode = BlendMode.Xor
                ),
            )
        }
    }
}