package io.github.gthght.stegandro.presentation.extract.result

data class ExtractResultState(
    val messageResult: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)