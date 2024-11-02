package io.github.gthght.stegandro.presentation.embed.result

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.gthght.stegandro.domain.repository.ImageRepository
import io.github.gthght.stegandro.presentation.common.UiState
import io.github.gthght.stegandro.util.getImageMetaDataFromUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EmbedResultViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel() {

    var state by mutableStateOf(EmbedResultState())
        private set

    suspend fun embedImage(uri: Uri, key: String, message: String, isSampled: Boolean) {
        state = state.copy(isLoading = true)
        withContext(Dispatchers.IO) {
            state = when (val result = imageRepository.embedImage(uri, key = key, message = message, isSampled = isSampled)) {
                is UiState.Loading -> {
                    state.copy(isLoading = true)
                }

                is UiState.Success -> {
                    state.copy(resultPath = result.data, isLoading = false)
                }

                is UiState.Error -> {
                    state.copy(error = result.errorMessage, isLoading = false)
                }
            }
        }
    }

    fun loadImageMetaData(context: Context){
        state.resultPath?.let {
            state = state.copy(resultMetaData = getImageMetaDataFromUri(context, it))
        }
    }

    suspend fun copyImageToDestination(destinationPath: Uri) {
        val originalPath = state.resultPath
        if (originalPath == null) {
            state = state.copy(error = "Hasil Tidak Ditemukan")
            return
        }
        state = state.copy(snackBarMessage = null, isLoading = true)
        withContext(Dispatchers.IO) {
            state =
                when (val result =
                    imageRepository.copyStreamToFile(originalPath, destinationPath)) {
                    is UiState.Loading -> {
                        state.copy(isLoading = true)
                    }

                    is UiState.Success -> {
                        state.copy(snackBarMessage = "Menyalin Gambar Berhasil",isLoading = false)
                    }

                    is UiState.Error -> {
                        state.copy(error = result.errorMessage, isLoading = false)
                    }
                }
        }
    }

    fun setOriginalImageSize(value: Int){
        state = state.copy(originalSize = value)
    }
}