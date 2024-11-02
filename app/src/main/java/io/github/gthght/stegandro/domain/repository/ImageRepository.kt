package io.github.gthght.stegandro.domain.repository

import android.net.Uri
import io.github.gthght.stegandro.data.repository.ImageMetricsResult
import io.github.gthght.stegandro.presentation.common.UiState

interface ImageRepository {
    suspend fun countImageCapacity(
        imagePath: Uri,
        useCoilApi: Boolean
    ): UiState<Int>

    suspend fun embedImage(imagePath: Uri, key: String, message: String, isSampled: Boolean): UiState<Uri>
    suspend fun extractImage(imagePath: Uri, key: String): UiState<String>
    suspend fun compareImage(
        originalImagePath: Uri,
        modifiedImagePath: Uri
    ): UiState<ImageMetricsResult>

    suspend fun copyStreamToFile(
        originalPath: Uri,
        destinationPath: Uri
    ): UiState<Unit>
}