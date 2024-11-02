package io.github.gthght.stegandro.data.locale.jpg.process.subprocess

import io.github.gthght.stegandro.data.locale.util.EOI
import io.github.gthght.stegandro.data.locale.util.RST0
import io.github.gthght.stegandro.data.locale.util.RST7
import java.io.InputStream

@OptIn(ExperimentalStdlibApi::class)
class BitInputStream(private val inputStream: InputStream) {
    private var currentByte = 0
    private var bitCounter = 0

    fun nextBit(): Int {
        if (bitCounter == 0) {
            currentByte = inputStream.read()
            bitCounter = 8
            if (currentByte == 0xff) {
                val b2 = inputStream.read()
                if (b2 != 0) {
                    if (b2.toByte() in RST0..RST7) {
                        currentByte = inputStream.read()
                        bitCounter = 8
                    } else {
                        check(b2 != EOI.toInt()) { "end" }
                        throw IllegalStateException("should never happen = ${b2.toHexString()}")
                    }
                }
            }

            // There can be a Restart Interval Marker in the middle Of Byte and it's hard to detect and rollback
//            byteHistory = (byteHistory shl 8) or currentByte
//            if(compare16BitValue(byteHistory, 0xffd0)){
//                println("Reset Interval Found In The Middle Of Bytes")
//                println(byteHistory.toHexString())
//                align()
//            }
//            println(currentByte.toByte().toHexString())
        }

        val bit = (currentByte shr 7) and 1
        bitCounter--
        currentByte = currentByte shl 1

        return bit
    }

    fun compare16BitValue(first: Int, second: Int): Boolean{
        val firstAltered = first and 0b1111111111111111
        val secondAltered = second and 0b1111111111111111
        return firstAltered == secondAltered
    }
    fun compare8BitValue(first: Int, second: Int): Boolean{
        val firstAltered = first and 0b11111111
        val secondAltered = second and 0b11111111
        return firstAltered == secondAltered
    }

    fun align() {
        if (bitCounter != 0) {
            currentByte = inputStream.read()
            bitCounter = 8
        }
    }
}
