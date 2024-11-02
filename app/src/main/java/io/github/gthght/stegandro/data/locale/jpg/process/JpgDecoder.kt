package io.github.gthght.stegandro.data.locale.jpg.process

import io.github.gthght.stegandro.data.locale.jpg.data.segment.DhtSegment
import io.github.gthght.stegandro.data.locale.jpg.data.segment.DqtSegment
import io.github.gthght.stegandro.data.locale.jpg.data.segment.SofSegment
import io.github.gthght.stegandro.data.locale.jpg.data.segment.SosSegment
import io.github.gthght.stegandro.data.locale.jpg.data.structure.ImageBlock
import io.github.gthght.stegandro.data.locale.jpg.data.structure.JpgHeader
import io.github.gthght.stegandro.data.locale.util.APP0
import io.github.gthght.stegandro.data.locale.util.APP15
import io.github.gthght.stegandro.data.locale.util.COM
import io.github.gthght.stegandro.data.locale.util.DHP
import io.github.gthght.stegandro.data.locale.util.DHT
import io.github.gthght.stegandro.data.locale.util.DNL
import io.github.gthght.stegandro.data.locale.util.DQT
import io.github.gthght.stegandro.data.locale.util.DRI
import io.github.gthght.stegandro.data.locale.util.EOI
import io.github.gthght.stegandro.data.locale.util.EXP
import io.github.gthght.stegandro.data.locale.util.JPG0
import io.github.gthght.stegandro.data.locale.util.JPG13
import io.github.gthght.stegandro.data.locale.util.RST0
import io.github.gthght.stegandro.data.locale.util.RST7
import io.github.gthght.stegandro.data.locale.util.SOF0
import io.github.gthght.stegandro.data.locale.util.SOI
import io.github.gthght.stegandro.data.locale.util.SOS
import io.github.gthght.stegandro.data.locale.util.TEM
import java.io.InputStream


@OptIn(ExperimentalStdlibApi::class)
class JpgDecoder(private val inputStream: InputStream) {

    var jpgHeader = JpgHeader()

    @OptIn(ExperimentalStdlibApi::class)
    fun decodeHeader(): String {
        var comment = ""
        val firstByte = inputStream.read()
        val secondByte = inputStream.read()
        if (firstByte != 0xFF && secondByte != 0xD8) {
            throw IllegalArgumentException()
        }
        var isReadingHeader = true
        while (isReadingHeader) {
            val data = inputStream.read()
            if (data == -1) {
                isReadingHeader = false
            }
            if (data == 0xFF) {
                when (val current = inputStream.read().toByte()) {
                    in APP0..APP15 -> {
                        readAppn()
                    }

                    DQT -> {
                        val dqtTables = DqtSegment.decode(inputStream)
                        jpgHeader.dqtSegment.add(dqtTables)
                    }

                    DRI -> {
                        jpgHeader.restartInterval = readRestartIntervalMarker(inputStream)
                    }

                    DHT -> {
                        val dhtTables = DhtSegment.decode(inputStream)
                        jpgHeader.dhtSegment.add(dhtTables)
                    }

                    SOF0 -> {
                        val sof0Segment = SofSegment.decode(inputStream)
                        jpgHeader.apply {
                            sofSegment = sof0Segment
                            mcuWidth = (sof0Segment.width+ 7) / 8
                            mcuHeight = (sof0Segment.height+ 7) / 8
                            vSF = sof0Segment.components.maxOf { it.vSF }
                            hSF = sof0Segment.components.maxOf { it.hSF }
                            mcuWidthReal = mcuWidth
                            mcuHeightReal = mcuHeight
                            if (hSF==2 && mcuWidth % 2 == 1){
                                mcuWidthReal++
                            }
                            if (vSF==2 && mcuHeight % 2 == 1){
                                mcuHeightReal++
                            }
                        }
                    }

                    SOS -> {
                        jpgHeader.sosSegment = SosSegment.decode(inputStream, jpgHeader.sofSegment.zeroBased)
                        isReadingHeader = false
                    }

                    COM -> {
                        comment = readComment()
                    }

                    // Ignore Marker
                    in JPG0..JPG13 -> {
                        skipMarker(current)
                    }

                    DNL, DHP, EXP -> {
                        skipMarker(current)
                    }

                    TEM -> {
                        // Do Nothing
                    }

                    0xFF.toByte() -> {
                        continue
                    }

                    // Invalid Marker Found
                    SOI -> {
                        return comment
                    }

                    EOI -> {
                        return comment
                    }

                    in RST0..RST7 -> {
                        return comment
                    }

                    else -> {
                        return comment
                    }
                }
            }
        }
        jpgHeader.blocks = MutableList(jpgHeader.mcuWidthReal * jpgHeader.mcuHeightReal){
            ImageBlock()
        }
        return comment
    }

    // skip APPN marker
    private fun readAppn() {
        val firstByte = inputStream.read()
        val secondByte = inputStream.read()

        val length = (firstByte shl 8) + secondByte

        for (i in 1..length - 2) {
            inputStream.read()
        }
    }

    private fun readComment(): String {
        val comment : MutableList<Byte> = mutableListOf()
        val firstByte = inputStream.read()
        val secondByte = inputStream.read()

        val length = (firstByte shl 8) + secondByte

        for (i in 1..length - 2) {
            comment.add(inputStream.read().toByte())
        }
        return comment.toByteArray().toString(Charsets.ISO_8859_1)
    }

    // skip unsupported / future marker
    private fun skipMarker(number: Number) {

        val firstByte = inputStream.read()
        val secondByte = inputStream.read()

        val length = (firstByte shl 8) + secondByte


        for (i in 1..length - 2) {
            inputStream.read()
        }
    }

    companion object{
        fun readRestartIntervalMarker(inputStream: InputStream): Int {

            val firstByte = inputStream.read()
            val secondByte = inputStream.read()

            val length = (firstByte shl 8) + secondByte

            val restartInterval = (inputStream.read() shl 8) + inputStream.read()

            if (length - 4 != 0) {
                throw Exception("Restart Interval Marker is invalid")

            }
            return restartInterval
        }
    }
}