package io.github.gthght.stegandro.data.locale.jpg.process.subprocess

import io.github.gthght.stegandro.data.locale.jpg.data.structure.ImageBlock
import io.github.gthght.stegandro.data.locale.util.reverseZigzagMap

class ZigzagScanner {
    companion object {
        fun zigzagImageBlocks(blocks: List<ImageBlock>) {
            for (block in blocks) {
                for (cIndex in 0 until 3) {
                    val tempMcu = IntArray(64)
                    for (pIndex in 0 until 64) {
                        tempMcu[reverseZigzagMap[pIndex]] = block.getC(cIndex)[pIndex]
                    }
                    for (pIndex in 0 until 64){
                        block.getC(cIndex)[pIndex] = tempMcu[pIndex]
                    }
                }
            }
        }
    }
}