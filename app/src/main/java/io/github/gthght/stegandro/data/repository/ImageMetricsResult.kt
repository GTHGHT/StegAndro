package io.github.gthght.stegandro.data.repository

data class ImageMetricsResult(
    val mseChannelOne: Double? = null,
    val mseChannelTwo: Double? = null,
    val mseChannelThree: Double? = null,
    val mseCombined: Double? = null,
    val psnr: Double? = null
)
