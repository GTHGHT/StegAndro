package io.github.gthght.stegandro.data.locale.jpg.process.subprocess

import kotlin.experimental.or

class BitWriter(private val data: MutableList<Byte>) {

    private var nextBit = 0

    private fun writeBit(bit: Int) {
        if (nextBit == 0) {
            data.add(0)
        }
        data[data.lastIndex] = data[data.lastIndex] or ((bit and 1) shl (7 - nextBit)).toByte()
        nextBit = (nextBit + 1) % 8
        if (nextBit == 0 && data.last() == 0xFF.toByte()) {
            data.add(0)
        }
    }

    fun writeBits(bits: Int, length: Int) {
        for (i in 1..length) {
            writeBit(bits shr (length - i))
        }
    }
}
