package io.github.gthght.stegandro.util

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


private const val FILENAME_FORMAT = "yyMd_HHmmss"
private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

fun getImageUri(context: Context, affix:String=""): Uri {
    var uri: Uri? = null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$affix$timeStamp.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/StegAndro/")
        }
        uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }
    return uri ?: getImageUriForPreQ(context,affix)
}

private fun getImageUriForPreQ(context: Context, affix:String): Uri {
    val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imageFile = File(filesDir, "Pictures/StegAndro/$affix$timeStamp.jpg")
    if (imageFile.parentFile?.exists() == false) imageFile.parentFile?.mkdir()
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )
}

private const val MAXIMAL_SIZE = 1000000

suspend fun reduceImageSize(context: Context, originalImage: Uri): Bitmap =
    withContext(Dispatchers.IO) {
        val destinationImage = getImageUri(context)
        val bitmap = getBitmap(context.contentResolver, originalImage)
        var compressQuality = 90
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > MAXIMAL_SIZE)
        val destinationOutput = context.contentResolver.openOutputStream(destinationImage)
        destinationOutput?.let {
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, it)
            it.close()
        }
        return@withContext bitmap
    }

fun getBitmap(contentResolver: ContentResolver, fileUri: Uri, isSampled: Boolean = false): Bitmap {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(contentResolver, fileUri)
            ) { imageDecoder, imageInfo, _ ->
                imageDecoder.isMutableRequired = true
                if (isSampled) {
                    val width = imageInfo.size.width
                    val height = imageInfo.size.height
                    val ratio = width.toDouble() / height.toDouble()

                    if(width > 1200 && height > 1200) {
                        imageDecoder.setTargetSize(1200, (1200.toDouble() / ratio).toInt())
                    }
                }
            }
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(contentResolver, fileUri)
        }
    } catch (e: Exception) {
        throw FileNotFoundException("Gambar Tidak Ditemukan = ${e.message}")
    }
}

suspend fun getBitmap(context: Context, fileUri: Uri): Bitmap =
    coroutineScope {
        try {
            val loader = ImageLoader(context)
            val request =
                ImageRequest.Builder(context).data(fileUri).allowHardware(false).build()
            val result = (loader.execute(request) as SuccessResult).drawable
            return@coroutineScope result.toBitmap()
        } catch (e: Exception) {
            throw FileNotFoundException("Gambar Tidak Ditemukan")
        }
    }

fun getImageMetaDataFromUri(context: Context, fileUri:Uri?): ImageMetaData? {
    var cursor: Cursor? = null
    val projection = arrayOf(
        "volume_name",
        "relative_path",
        "_display_name",
        "resolution",
        "_size",
    )
    try {
        if (fileUri == null) return null
        cursor = context.contentResolver.query(fileUri, projection, null, null,
            null)
        if (cursor != null && cursor.moveToFirst()) {

            val volumeNameIndex = cursor.getColumnIndexOrThrow(projection[0])
            val relativePathIndex = cursor.getColumnIndexOrThrow(projection[1])
            val displayNameIndex = cursor.getColumnIndexOrThrow(projection[2])
            val resolutionIndex = cursor.getColumnIndexOrThrow(projection[3])
            val sizeIndex = cursor.getColumnIndexOrThrow(projection[4])
            return ImageMetaData(
                cursor.getString(volumeNameIndex),
                cursor.getString(relativePathIndex),
                cursor.getString(displayNameIndex),
                cursor.getString(resolutionIndex),
                cursor.getInt(sizeIndex)
            )
        }
    } finally {
        cursor?.close()
    }
    return null
}

fun getImageSizeFromURI(context: Context, uri:Uri?): Int{
    if (uri == null) return -1

    var cursor: Cursor? = null
    val projection = arrayOf(
        "_size",
    )
    try {
        cursor = context.contentResolver.query(uri, projection, null, null,
            null)
        if (cursor != null && cursor.moveToFirst()) {

            val sizeIndex = cursor.getColumnIndexOrThrow("_size")
            return cursor.getInt(sizeIndex)
        }
    } finally {
        cursor?.close()
    }
    return -1
}