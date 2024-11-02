package io.github.gthght.stegandro.data.locale.jpg.data.segment.subsegment


class DqtTable(val id: Int, val data: IntArray = IntArray(64), val precision: Int = 0){
    fun dequantizeBlock(block: IntArray) {
        for (i in 0 until 64) {
            block[i] *= data[i]
        }
    }

    fun quantizeBlock(block: IntArray) {
        for (i in 0 until 64) {
            block[i] /= data[i]
        }
    }
}
