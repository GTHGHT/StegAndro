package io.github.gthght.stegandro.presentation.embed

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.gthght.stegandro.domain.repository.ImageRepository
import io.github.gthght.stegandro.presentation.common.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.charset.StandardCharsets
import javax.inject.Inject


@HiltViewModel
class EmbedViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel() {

    var state by mutableStateOf(EmbedState())
        private set

    suspend fun setImage(uri: Uri, fromCamera: Boolean) {
        state = state.copy(imagePathName = uri, fromCamera = fromCamera, isLoading = true, imageCapacity = null, error = null)
        withContext(Dispatchers.IO) {
            state = when (val result = imageRepository.countImageCapacity(uri, fromCamera)) {
                is UiState.Loading -> {
                    state.copy(isLoading = true)
                }

                is UiState.Success -> {
                    state.copy(imageCapacity = result.data, isLoading = false)
                }

                is UiState.Error -> {
                    state.copy(error = result.errorMessage, isLoading = false)
                }
            }
        }
    }


    fun validateInput(key: String, message: String): Boolean {
        val encoder = StandardCharsets.ISO_8859_1.newEncoder()
        state = state.copy(error = null)
        return if (state.imagePathName == null) {
            state = state.copy(error = "Pilih gambar terlebih dahulu")
            false
        } else if (key.isBlank()) {
            state = state.copy(error = "Kunci tidak boleh kosong")
            false
        } else if (message.isBlank()) {
            state = state.copy(error = "Pesan tidak boleh kosong")
            false
        } else if (key.length > message.length) {
            state = state.copy(error = "Kunci tidak boleh melebihi panjang pesan")
            false
        } else if (message.length > (state.imageCapacity ?: 0)) {
            state = state.copy(error = "Panjang pesan tidak boleh melebihi kapasitas gambar")
            false
        } else if (!encoder.canEncode(key)) {
            state = state.copy(error = "Kunci memiliki karakter yang tidak didukung")
            false
        } else if (!encoder.canEncode(message)) {
            state = state.copy(error = "Pesan memiliki karakter yang tidak didukung")
            true
        } else {
            true
        }
    }

}