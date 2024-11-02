package io.github.gthght.stegandro.presentation.extract.result

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
import javax.inject.Inject

@HiltViewModel
class ExtractResultViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel() {

    var state by mutableStateOf(ExtractResultState())
        private set

    suspend fun extractImage(uri: Uri, key: String){
        state = state.copy(isLoading = true)
        withContext(Dispatchers.IO) {
            state = when (val result = imageRepository.extractImage(uri, key)) {
                is UiState.Loading -> {
                    state.copy(isLoading = true)
                }

                is UiState.Success -> {
                    state.copy(messageResult = result.data, isLoading = false)
                }

                is UiState.Error -> {
                    state.copy(error = result.errorMessage, isLoading = false)
                }
            }
        }

    }
}