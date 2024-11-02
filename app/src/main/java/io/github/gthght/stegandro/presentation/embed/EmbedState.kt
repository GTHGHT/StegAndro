package io.github.gthght.stegandro.presentation.embed

import android.net.Uri

data class EmbedState(
    val imagePathName: Uri? = null,
    val imageCapacity: Int? = null,
    val fromCamera: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
