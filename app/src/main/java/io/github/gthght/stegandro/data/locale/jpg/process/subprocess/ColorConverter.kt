package io.github.gthght.stegandro.data.locale.jpg.process.subprocess

import io.github.gthght.stegandro.data.locale.jpg.data.structure.ImageBlock

class ColorConverter {
    companion object {
        private fun rgbToYCbCrBlock(block: ImageBlock) {
            for (y in 0 until 8) {
                for (x in 0 until 8) {
                    val pixel = y * 8 + x
                    var y = (0.2990 * block.cOne[pixel] + 0.5870 * block.cTwo[pixel] + 0.1140 * block.cThree[pixel] - 128).toInt()
                    var cb = (-0.1687 * block.cOne[pixel] - 0.3313 * block.cTwo[pixel] + 0.5000 * block.cThree[pixel]).toInt()
                    var cr = (0.5000 * block.cOne[pixel] - 0.4187 * block.cTwo[pixel] - 0.0813 * block.cThree[pixel]).toInt()
                    y = y.coerceIn(-128, 127)
                    cb = cb.coerceIn(-128, 127)
                    cr = cr.coerceIn(-128, 127)
                    block.cOne[pixel] = y
                    block.cTwo[pixel] = cb
                    block.cThree[pixel] = cr
                }
            }
        }

        fun rgbToYCbCr(image: List<ImageBlock>) {
            for (element in image){
                rgbToYCbCrBlock(element)
            }
        }


        fun ycbcrToRgbBlock(yBlock: ImageBlock, cbcrBlock: ImageBlock, vSF: Int, hSF: Int, v: Int, h: Int) {
            for (y in 7 downTo 0) {
                for (x in 7 downTo 0) {
                    val pixel = y * 8 + x
                    val cbcrPixelRow = y / vSF + 4 * v
                    val cbcrPixelColumn = x / hSF + 4 * h
                    val cbcrPixel = cbcrPixelRow * 8 + cbcrPixelColumn
                    var r = yBlock.cOne[pixel] + (1.402f * cbcrBlock.cThree[cbcrPixel]).toInt() + 128
                    var g = yBlock.cOne[pixel] - (0.344f * cbcrBlock.cTwo[cbcrPixel]).toInt() - (0.714f * cbcrBlock.cThree[cbcrPixel]).toInt() + 128
                    var b = yBlock.cOne[pixel] + (1.772f * cbcrBlock.cTwo[cbcrPixel]).toInt() + 128
                    r=r.coerceIn(0,255)
                    g=g.coerceIn(0,255)
                    b=b.coerceIn(0,255)
                    yBlock.cOne[pixel] = r
                    yBlock.cTwo[pixel] = g
                    yBlock.cThree[pixel] = b
                }
            }
        }
    }
}