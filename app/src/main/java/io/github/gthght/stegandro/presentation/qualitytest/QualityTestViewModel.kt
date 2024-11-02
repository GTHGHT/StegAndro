package io.github.gthght.stegandro.presentation.qualitytest

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.gthght.stegandro.data.repository.ImageMetricsResult
import io.github.gthght.stegandro.domain.repository.ImageRepository
import io.github.gthght.stegandro.presentation.common.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class QualityTestViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel() {
    var state by mutableStateOf(QualityTestState())
        private set

    fun setOriginalImage(uri: Uri) {
        state = state.copy(originalImage = uri)
    }

    fun setModifiedImage(uri: Uri) {
        state = state.copy(modifiedImage = uri)
    }

    suspend fun runStatistic() {
        state = state.copy(isLoading = true, error = null, metricsResult = ImageMetricsResult())
        withContext(Dispatchers.IO) {
            state.originalImage?.let { originalPath ->
                state.modifiedImage?.let { modifiedPath ->
                    state =
                        when (val result =
                            imageRepository.compareImage(originalPath, modifiedPath)) {
                            is UiState.Loading -> {
                                state.copy(isLoading = true)
                            }

                            is UiState.Success -> {

                                state.copy(metricsResult = result.data, isLoading = false)
                            }

                            is UiState.Error -> {
                                state.copy(error = result.errorMessage, isLoading = false)
                            }
                        }
                }
            }
        }
    }

}