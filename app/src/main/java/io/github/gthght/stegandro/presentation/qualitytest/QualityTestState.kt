package io.github.gthght.stegandro.presentation.qualitytest

import android.net.Uri
import io.github.gthght.stegandro.data.repository.ImageMetricsResult

data class QualityTestState (
    val originalImage: Uri? = null,
    val modifiedImage: Uri? = null,
    val metricsResult: ImageMetricsResult = ImageMetricsResult(),
    val isLoading: Boolean = false,
    val error: String? = null
)