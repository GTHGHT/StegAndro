package io.github.gthght.stegandro.presentation.embed.result

import android.net.Uri
import io.github.gthght.stegandro.util.ImageMetaData

data class EmbedResultState(
    val resultPath: Uri? = null,
    val resultMetaData: ImageMetaData? = null,
    val originalSize: Int = -1,
    val isLoading: Boolean = false,
    val snackBarMessage: String? = null,
    val error: String? = null
)