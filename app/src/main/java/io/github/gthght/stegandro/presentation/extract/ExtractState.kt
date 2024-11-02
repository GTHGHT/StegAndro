package io.github.gthght.stegandro.presentation.extract

import android.net.Uri

data class ExtractState(
    val imagePathName: Uri? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
