package io.github.gthght.stegandro.data.locale.jpg.data.segment

import io.github.gthght.stegandro.data.locale.jpg.data.segment.subsegment.SofComponent
import io.github.gthght.stegandro.data.locale.util.getFirstHalfByte
import io.github.gthght.stegandro.data.locale.util.getLastHalfByte
import java.io.InputStream

class SofSegment(
    private val precision: Int = 0,
    val width: Int = 0,
    val height: Int = 0,
    val components: List<SofComponent> = listOf(),
    val zeroBased: Boolean = false,
    val isSet: Boolean = false
    ) {

    val componentCount: Int
        get() = components.size

    fun printInfo(){
        println("SOF0 Segment:")
        println("Precision: $precision, Width: $width, Height: $height, zeroBased: $zeroBased")
        println("$componentCount Components= ")
        components.forEach {
            println("id: ${it.id}, hSF: ${it.hSF}, vSF: ${it.vSF}, qtId: ${it.qtId}")
        }
    }

    companion object {
        fun decode(inputStream: InputStream): SofSegment {
            val length = (inputStream.read() shl 8) + inputStream.read()

            val precision = inputStream.read()
            if (precision != 8) {
                throw Exception("Precision is Invalid")
            }

            val height = (inputStream.read() shl 8) + inputStream.read()
            val width = (inputStream.read() shl 8) + inputStream.read()

            if (height == 0 || width == 0) {
                throw Exception("Height or Width should not be Zero")
            }

            val numChannel = inputStream.read()
            if (numChannel == 4) {
                throw Exception("CMYK Color Channel Is Not Supported")
            } else if (numChannel == 0) {
                throw Exception("Number Of Color Channel should not be Zero")
            }

            val components: MutableList<SofComponent> = mutableListOf()
            var zeroBased = false
            for (i in 0 until numChannel) {
                var componentId = inputStream.read()
                if (componentId == 0) {
                    zeroBased = true
                }
                if (zeroBased) {
                    componentId += 1
                }
                if (componentId == 4 || componentId == 5) {
                    throw Exception("YIQ color mode is not supported")
                } else if (componentId == 0 || componentId > 3) {
                    throw Exception("Component ID is invalid")
                }

                val samplingFactor = inputStream.read()
                val hSamplingFactor = getFirstHalfByte(samplingFactor)
                val vSamplingFactor = getLastHalfByte(samplingFactor)
                val qTableId = inputStream.read()
                // Quantization Have At Max. 4 Table
                if (qTableId > 3) {
                    throw Exception("Quantization Table ID is Invalid")
                }
                components.add(componentId - 1, SofComponent(componentId, hSamplingFactor, vSamplingFactor, qTableId))
            }
            if (numChannel == 1) {
                components.removeAt(1)
                components.removeAt(2)
            }
            if (length - 8 - (3 * numChannel) != 0) {
                throw Exception("SOF 0 Marker Is Invalid")
            }
            return SofSegment(precision, width, height, components, zeroBased, true)
        }
    }
}