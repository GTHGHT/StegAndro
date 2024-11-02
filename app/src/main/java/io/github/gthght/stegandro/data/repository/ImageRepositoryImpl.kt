package io.github.gthght.stegandro.data.repository

import android.net.Uri
import io.github.gthght.stegandro.data.locale.SteganographyInterface
import io.github.gthght.stegandro.domain.repository.ImageRepository
import io.github.gthght.stegandro.presentation.common.UiState
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val steganography: SteganographyInterface
) : ImageRepository {

    override suspend fun countImageCapacity(
        imagePath: Uri,
        useCoilApi: Boolean
    ): UiState<Int> {
        return try {
            UiState.Success(
                data = steganography.checkPayloadCapacity(imagePath, useCoilApi)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            UiState.Error(e.message ?: "An unknown error has occurred")
        }
    }

    override suspend fun embedImage(imagePath: Uri, key: String, message: String, isSampled: Boolean): UiState<Uri> {
        return try {
            UiState.Success(
                data = steganography.encodeImage(imagePath, key = key, message = message, isSampled)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            UiState.Error(e.message ?: "An unknown error has occurred")
        }
    }

    override suspend fun extractImage(
        imagePath: Uri,
        key: String
    ): UiState<String> {
        return try {
            UiState.Success(
                data = steganography.decodeImage(imagePath, key)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            UiState.Error(e.message ?: "An unknown error has occurred")
        }
    }

    override suspend fun compareImage(
        originalImagePath: Uri,
        modifiedImagePath: Uri
    ): UiState<ImageMetricsResult> {
        return try {
            UiState.Success(
                data = steganography.compareImage(originalImagePath, modifiedImagePath)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            UiState.Error(e.message ?: "An unknown error has occurred")
        }
    }

    override suspend fun copyStreamToFile(originalPath: Uri, destinationPath: Uri): UiState<Unit> {
        return try {
            UiState.Success(
                data = steganography.copyImageToDestination(originalPath, destinationPath)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            UiState.Error(e.message ?: "An unknown error has occurred")
        }
    }


}