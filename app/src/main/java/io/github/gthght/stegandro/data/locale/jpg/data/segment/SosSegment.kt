package io.github.gthght.stegandro.data.locale.jpg.data.segment

import io.github.gthght.stegandro.data.locale.jpg.data.segment.subsegment.SosComponent
import io.github.gthght.stegandro.data.locale.util.getFirstHalfByte
import io.github.gthght.stegandro.data.locale.util.getLastHalfByte
import java.io.InputStream

class SosSegment(
    val components: List<SosComponent> = listOf(),
    var numComponent: Int = 0,
    var startSelection: Int = 0,
    var endSelection: Int = 63,
    var successiveApproximateHigh: Int = 0,
    var successiveApproximateLow: Int = 0,
    var isSet: Boolean = false
) {

    fun printInfo(){
        println("SOS Segment:")
        println("Start Selection: $startSelection, End Selection: $endSelection")
        println("Successive Approximation High: $successiveApproximateHigh, Low: $successiveApproximateLow")
        println("$numComponent Components= ")
        components.forEach {
            println("ID: ${it.id}, AC DHT Table ID: ${it.acDhtTableId}, AC DHT Table ID: ${it.dcDhtTableId}")
        }
    }

    companion object {
        fun decode(inputStream: InputStream, zeroBased: Boolean): SosSegment {
            var length = (inputStream.read() shl 8) + inputStream.read()

            val numComponentsInScan = inputStream.read()
            val componentList: MutableList<SosComponent> = mutableListOf()
            for (i in 0 until numComponentsInScan) {
                var compId = inputStream.read()

                if (zeroBased) {
                    compId += 1
                }

                if (compId > numComponentsInScan) {
                    throw Exception("Invalid Color Component ID: $compId")
                }

                val huffmanTableID = inputStream.read()
                val dcTableId = getFirstHalfByte(huffmanTableID)
                val acTableId = getLastHalfByte(huffmanTableID)
                if (dcTableId > 3) {
                    throw Exception("Invalid DC Huffman Table ID: $dcTableId")
                }
                if (acTableId > 3) {
                    throw Exception("Invalid AC Huffman Table ID: $acTableId")
                }
                componentList.add(SosComponent(compId, dcTableId, acTableId))
                length -= 2
            }
            val startOfSelection = inputStream.read()
            val endOfSelection = inputStream.read()
            val successiveApprox = inputStream.read()
            val successiveApproxHigh = getFirstHalfByte(successiveApprox)
            val successiveApproxLow = getLastHalfByte(successiveApprox)

            // Baseline JPG tidak menggunakan spectral selection atau successive approximation
            if (startOfSelection != 0 || endOfSelection != 63) {
                throw Exception("Spectral Selection is Invalid For Baseline JPG")
            }
            if (successiveApproxHigh != 0 || successiveApproxLow != 0) {
                throw Exception("Successive Approximation is Invalid For Baseline JPG")
            }

            if (length - 6 != 0) {
                throw Exception("Start Of Scan Marker Invalid")
            }

            return SosSegment(
                componentList,
                numComponentsInScan,
                startOfSelection,
                endOfSelection,
                successiveApproxHigh,
                successiveApproxLow,
                isSet = true
            )
        }
    }
}