package io.github.gthght.stegandro.data.locale.util

import android.graphics.Bitmap
import io.github.gthght.stegandro.data.locale.jpg.data.structure.ImageBlock
import kotlin.math.min

class BitmapReader {
    companion object {
        fun getImageBlocks(bufferedImage: Bitmap): List<ImageBlock> {
            val w = bufferedImage.width
            val h = bufferedImage.height

            val blocksWidth = (w + 7) / 8
            val blocksHeight = (h + 7) / 8

            return (0 until blocksWidth * blocksHeight).map { i ->
                val blockRow = i % blocksWidth
                val blockColumn = i / blocksWidth
                ImageBlock().also { block ->
                    for (i in 0 until 64) {
                        val x = blockRow * 8
                        val y = blockColumn * 8
                        val pixelX = min(x + i % 8, w - 1)
                        val pixelY = min(y + i / 8, h - 1)
                        bufferedImage.getPixel(pixelX, pixelY).let {
                            block.cOne[i] = it and 0xff0000 shr 16
                            block.cTwo[i] = it and 0xff00 shr 8
                            block.cThree[i] = it and 0xff
                        }
                    }
                    block.isCOneOnly = false
                }
            }
        }
    }
}
