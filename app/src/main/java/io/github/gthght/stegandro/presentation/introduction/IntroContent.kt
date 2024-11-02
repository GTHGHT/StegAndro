package io.github.gthght.stegandro.presentation.introduction

import androidx.compose.ui.graphics.painter.Painter

data class IntroContent(
    val title: String,
    val description: String,
    val introImage: Painter,
    val contentDescription: String?
)
