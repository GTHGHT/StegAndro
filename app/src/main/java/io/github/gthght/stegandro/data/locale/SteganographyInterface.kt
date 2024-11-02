package io.github.gthght.stegandro.data.locale

import android.net.Uri
import io.github.gthght.stegandro.data.repository.ImageMetricsResult
import java.io.InputStream

interface SteganographyInterface {
    suspend fun checkPayloadCapacity(
        uri: Uri,
        isSampled: Boolean
    ): Int

    suspend fun encodeImage(uri: Uri, key: String, message: String, isSampled: Boolean): Uri

    suspend fun decodeImage(inputStream: InputStream, key: String): String

    suspend fun decodeImage(uri: Uri, key: String): String

    suspend fun compareImage(
        originalImagePath: Uri,
        modifiedImagePath: Uri
    ): ImageMetricsResult

    suspend fun copyImageToDestination(
        originalPath: Uri,
        destinationPath: Uri
    )
}