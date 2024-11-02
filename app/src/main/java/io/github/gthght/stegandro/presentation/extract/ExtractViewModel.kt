package io.github.gthght.stegandro.presentation.extract

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@HiltViewModel
class ExtractViewModel @Inject constructor(
) : ViewModel() {
    var state by mutableStateOf(ExtractState())
        private set

    fun setImage(uri: Uri) {
        state = state.copy(imagePathName = uri)
    }

    fun validateInput(key: String): Boolean {
        val encoder = StandardCharsets.ISO_8859_1.newEncoder()
        state = state.copy(error = null)
        return if (state.imagePathName == null) {
            state = state.copy(error = "Pilih gambar terlebih dahulu")
            false
        } else if (key.isBlank()) {
            state = state.copy(error = "Kunci tidak boleh kosong")
            false
        } else if (!encoder.canEncode(key)) {
            state = state.copy(error = "Kunci memiliki karakter yang tidak didukung")
            false
        } else {
            true
        }
    }
}