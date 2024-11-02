package io.github.gthght.stegandro.data.locale.util

import android.graphics.Bitmap
import kotlin.math.log10
import kotlin.math.pow

class ImageMetrics {

    companion object {
        private const val K1 = 0.01f
        private const val K2 = 0.03f

        private fun calculateMean(data: DoubleArray): Double {
            val sum = data.sum()
            return sum / data.size
        }

        private fun calculateVariance(data: DoubleArray, mean: Double): Double {
            val squaredDifferences = data.map { (it - mean).pow(2.0) }
            return squaredDifferences.sum() / data.size
        }

        private fun calculateCovariance(
            data1: DoubleArray,
            data2: DoubleArray,
            mean1: Double,
            mean2: Double
        ): Double {
            val products = data1.zip(data2).map { (a, b) -> (a - mean1) * (b - mean2) }
            return products.sum() / data1.size
        }

        private fun calculateMSSIM(data1: DoubleArray, data2: DoubleArray): Double {
            val mean1 = calculateMean(data1)
            val mean2 = calculateMean(data2)
            val variance1 = calculateVariance(data1, mean1)
            val variance2 = calculateVariance(data2, mean2)
            val covariance = calculateCovariance(data1, data2, mean1, mean2)

            val l = 255.0
            val c1 = (K1 * l).pow(2.0)
            val c2 = (K2 * l).pow(2.0)
            val num = (2.0 * mean1 * mean2 + c1) * (2.0 * covariance + c2)
            val den = (mean1 * mean1 + mean2 * mean2 + c1) * (variance1 + variance2 + c2)

            return num / den
        }

        fun structuralSimilarityGrayscale(image1: Bitmap, image2: Bitmap): Double {
            // Check for image dimension compatibility
            if (image1.width != image2.width || image1.height != image2.height) {
                throw IllegalArgumentException("Images must have the same dimensions")
            }

            val width = image1.width
            val height = image1.height
            val windowSize = 11 // Adjust window size as needed (usually odd)

            val data1 = DoubleArray(windowSize * windowSize)
            val data2 = DoubleArray(windowSize * windowSize)

            var mssim = 0.0

            // Loop through each pixel with a window
            for (y in 0 until height - windowSize + 1) {
                for (x in 0 until width - windowSize + 1) {
                    // Extract pixel values within the window for both images
                    loadDataWindowGrayscale(image1, data1, windowSize, x, y)
                    loadDataWindowGrayscale(image2, data2, windowSize, x, y)

                    // Calculate MSIM for this window
                    mssim += calculateMSSIM(data1, data2)
                }
            }

            // Average mssim values across all windows
            return mssim / ((width - windowSize + 1) * (height - windowSize + 1))
        }

        fun structuralSimilarityColor(image1: Bitmap, image2: Bitmap): Double {
            // Check for image dimension compatibility
            if (image1.width != image2.width || image1.height != image2.height) {
                throw IllegalArgumentException("Images must have the same dimensions")
            }

            val width = image1.width
            val height = image1.height
            val windowSize = 11 // Adjust window size as needed (usually odd)

            val y1 = DoubleArray(windowSize * windowSize)
            val cb1 = DoubleArray(windowSize * windowSize)
            val cr1 = DoubleArray(windowSize * windowSize)
            val y2 = DoubleArray(windowSize * windowSize)
            val cb2 = DoubleArray(windowSize * windowSize)
            val cr2 = DoubleArray(windowSize * windowSize)

            var mssimY = 0.0
            var mssimCb = 0.0
            var mssimCr = 0.0

            // Loop through each pixel with a window
            for (y in 0 until height - windowSize + 1) {
                for (x in 0 until width - windowSize + 1) {
                    // Extract pixel values within the window for both images
                    loadDataWindowColor(image1, y1, cb1, cr1, windowSize, x, y)
                    loadDataWindowColor(image2, y2, cb2, cr2, windowSize, x, y)

                    // Calculate MSIM for each channel (Y, Cb, Cr)
                    mssimY += calculateMSSIM(y1, y2)
                    mssimCb += calculateMSSIM(cb1, cb2)
                    mssimCr += calculateMSSIM(cr1, cr2)
                }
            }

            // Average mssim values across all windows and weight channels
            return (
                    (mssimY / ((width - windowSize + 1) * (height - windowSize + 1))) * 0.8 +
                            (mssimCb / ((width - windowSize + 1) * (height - windowSize + 1))) * 0.1 +
                            (mssimCr / ((width - windowSize + 1) * (height - windowSize + 1))) * 0.1
                    )
        }

        private fun loadDataWindowGrayscale(
            image: Bitmap,
            data: DoubleArray,
            windowSize: Int,
            x: Int,
            y: Int
        ) {
            for (wy in 0 until windowSize) {
                for (wx in 0 until windowSize) {
                    data[wy * windowSize + wx] = image.getPixel(x + wx, y + wy).let {
                        (it shr 16 and 0xff) * 0.2989 + (it shr 8 and 0xff) * 0.581 + (it and 0xff) * 0.114
                    }
                }
            }
        }

        private fun loadDataWindowColor(
            image: Bitmap,
            luma: DoubleArray,
            cb: DoubleArray,
            cr: DoubleArray,
            windowSize: Int,
            x: Int,
            y: Int
        ) {
            for (wy in 0 until windowSize) {
                for (wx in 0 until windowSize) {
                    val (red, green, blue) = image.getPixel(x, y).let {
                        Triple(
                            (it shr 16 and 0xff),
                            (it shr 8 and 0xff),
                            (it and 0xff)
                        )
                    }

                    // Calculate Y, Cb, Cr for both images at this pixel
                    luma[wy * windowSize + wx] = 0.2989 * red + 0.5870 * green + 0.1140 * blue
                    cb[wy * windowSize + wx] = 128 - 0.1687 * red - 0.3313 * green + 0.5000 * blue
                    cr[wy * windowSize + wx] = 128 + 0.5000 * red - 0.4187 * green - 0.0813 * blue
                }
            }
        }

        fun meanSquareError(
            image1: Bitmap,
            image2: Bitmap,
            isNormalized: Boolean = true
        ): Triple<Double, Double, Double> {
            // Check if image dimensions are equal
            if (image1.width != image2.width || image1.height != image2.height) {
                throw IllegalArgumentException("Gambar harus memiliki lebar dan tinggi yang sama.")
            }

            val width = image1.width
            val height = image1.height
//        var sumSquaredError = 0.0
            var sumC1 = 0.0
            var sumC2 = 0.0
            var sumC3 = 0.0

            // Loop through each pixel and calculate squared difference
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val normalizer = if (isNormalized) 255.0 else 1.0
                    val (red1, green1, blue1) = image1.getPixel(x, y).let {
                        Triple(
                            (it shr 16 and 0xff).toDouble() / normalizer,
                            (it shr 8 and 0xff).toDouble() / normalizer,
                            (it and 0xff).toDouble() / normalizer
                        )
                    }

                    val (red2, green2, blue2) = image2.getPixel(x, y).let {
                        Triple(
                            (it shr 16 and 0xff).toDouble() / normalizer,
                            (it shr 8 and 0xff).toDouble() / normalizer,
                            (it and 0xff).toDouble() / normalizer
                        )
                    }

                    val squaredDiffRed = (red1 - red2).pow(2.0)
                    val squaredDiffGreen = (green1 - green2).pow(2.0)
                    val squaredDiffBlue =
                        (blue1 - blue2).pow(2.0) // Swapped blue channels for consistency

//                sumSquaredError += squaredDiffRed + squaredDiffGreen + squaredDiffBlue
                    sumC1 += squaredDiffRed
                    sumC2 += squaredDiffGreen
                    sumC3 += squaredDiffBlue
                }
            }


            // Calculate mean squared error (consider averaging over all pixels or per channel)
            val totalPixels = width * height
            return Triple((sumC1 / totalPixels), (sumC2 / totalPixels), (sumC3 / totalPixels))
//        return sumSquaredError / (totalPixels * 3)  // Average MSE per channel (RGB)
        }

        fun peakSignalToNoiseRatio(mse: Double, isNormalized: Boolean = true): Double {
            if (mse == 0.0) return Double.POSITIVE_INFINITY
            val normalizer = if (isNormalized) 1.0 else 255.0
            return 10 * log10((normalizer).pow(2) / mse)
        }
    }
}