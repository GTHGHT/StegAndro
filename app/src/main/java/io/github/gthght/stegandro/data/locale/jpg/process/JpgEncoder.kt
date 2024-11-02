package io.github.gthght.stegandro.data.locale.jpg.process

import android.graphics.Bitmap
import io.github.gthght.stegandro.data.locale.jpg.data.huffman.HuffmanEncoderTable
import io.github.gthght.stegandro.data.locale.jpg.data.huffman.HuffmanNodeWrite
import io.github.gthght.stegandro.data.locale.jpg.data.segment.SosSegment
import io.github.gthght.stegandro.data.locale.jpg.data.segment.subsegment.SofComponent
import io.github.gthght.stegandro.data.locale.jpg.data.structure.ImageBlock
import io.github.gthght.stegandro.data.locale.jpg.data.structure.JpgHeader
import io.github.gthght.stegandro.data.locale.jpg.process.subprocess.BitWriter
import io.github.gthght.stegandro.data.locale.jpg.process.subprocess.ColorConverter
import io.github.gthght.stegandro.data.locale.jpg.process.subprocess.Dct
import io.github.gthght.stegandro.data.locale.util.APP0
import io.github.gthght.stegandro.data.locale.util.BitmapReader
import io.github.gthght.stegandro.data.locale.util.COM
import io.github.gthght.stegandro.data.locale.util.DHT
import io.github.gthght.stegandro.data.locale.util.DQT
import io.github.gthght.stegandro.data.locale.util.SOF0
import io.github.gthght.stegandro.data.locale.util.SOS
import io.github.gthght.stegandro.data.locale.util.bitLength
import io.github.gthght.stegandro.data.locale.util.packToByte
import io.github.gthght.stegandro.data.locale.util.packToInt
import io.github.gthght.stegandro.data.locale.util.toInt
import io.github.gthght.stegandro.data.locale.util.toOneComplement
import io.github.gthght.stegandro.data.locale.util.zigZagMap
import java.io.BufferedOutputStream
import java.io.IOException

class JpgEncoder(private val outputStream: BufferedOutputStream) {

    fun writeHeaders() {
        // The SOI(Start of Image) Marker
        val soiMarker = byteArrayOf(0xFF.toByte(), 0xD8.toByte())
        writeMarker(soiMarker, outputStream)
        writeJFIF()
    }


    private fun writeJFIF() {
        // The JFIF(JPEG Interchange Format Header)
        val jfif = ByteArray(18)
        jfif[0] = 0xFF.toByte() // APP0 Application Use Marker
        jfif[1] = APP0
        jfif[2] = 0x00.toByte() // Length of APP0 (2 Bytes)
        jfif[3] = 0x10.toByte()
        jfif[4] = 0x4A.toByte() // J
        jfif[5] = 0x46.toByte() // F
        jfif[6] = 0x49.toByte() // I
        jfif[7] = 0x46.toByte() // F
        jfif[8] = 0x00.toByte() // Space
        jfif[9] = 0x01.toByte() // JFIF Version 1.02 (2 Bytes)
        jfif[10] = 0x02.toByte()
        jfif[11] = 0x00.toByte() // units for X and Y Densities 0:no unit, 1: dot per inch, 2: dot per cm
        jfif[12] = 0x01.toByte() // Horizontal Pixel Density (2 Bytes)
        jfif[13] = 0x00.toByte()
        jfif[14] = 0x01.toByte() // Vertical Pixel Density (2 Bytes)
        jfif[15] = 0x00.toByte()
        jfif[16] = 0x00.toByte() // Thumbnail Horizontal Pixel Count
        jfif[17] = 0x00.toByte() // Thumbnail Vertical Pixel Count
        writeMarker(jfif, outputStream)
    }

    fun writeDQT(quantizationTable: IntArray, tableId: Int) {
        val dqtBytes = ByteArray(69)
        val dqtLength = 67
        dqtBytes[0] = 0xFF.toByte()
        dqtBytes[1] = DQT
        dqtBytes[2] = ((dqtLength shr 8) and 0xFF).toByte()//Marker Length
        dqtBytes[3] = (dqtLength and 0xFF).toByte()
        dqtBytes[4] = packToByte(0, tableId)//Precision, Table ID
        for (i in 0 until 64) {
            dqtBytes[i + 5] = quantizationTable[zigZagMap[i]].toByte()
        }
        writeMarker(dqtBytes, outputStream)
    }

    fun writeSOF0(width: Int, height: Int, components: List<SofComponent>) {
        val sofLength = 8 + (components.size * 3)
        val sofBytes = ByteArray(sofLength + 2)
        sofBytes[0] = 0xFF.toByte()
        sofBytes[1] = SOF0
        sofBytes[2] = ((sofLength shr 8) and 0xFF).toByte()
        sofBytes[3] = (sofLength and 0xFF).toByte()
        sofBytes[4] = 8 // Precision
        sofBytes[5] = ((height shr 8) and 0xFF).toByte()
        sofBytes[6] = (height and 0xFF).toByte()
        sofBytes[7] = ((width shr 8) and 0xFF).toByte()
        sofBytes[8] = (width and 0xFF).toByte()
        sofBytes[9] = components.size.toByte() // Number Of Color Channel
        for (i in components.indices) {
            sofBytes[9 + (i * 3) + 1] = components[i].id.toByte()
            sofBytes[9 + (i * 3) + 2] = packToByte(components[i].hSF, components[i].vSF)
            sofBytes[9 + (i * 3) + 3] = components[i].qtId.toByte()
        }
        writeMarker(sofBytes, outputStream)

    }

    fun writeDHT(huffmanTable: HuffmanEncoderTable, isAcTable: Boolean, tableId: Int) {
        val byteSize = 4 + 1 + 16 + huffmanTable.symbols.size
        val dhtLength = byteSize - 2
        val dhtBytes = ByteArray(byteSize)
        val dhtInfo = packToByte(isAcTable.toInt(), tableId)
        dhtBytes[0] = 0xFF.toByte()
        dhtBytes[1] = DHT
        dhtBytes[2] = ((dhtLength shr 8) and 0xFF).toByte()//Marker Length
        dhtBytes[3] = (dhtLength and 0xFF).toByte()//Marker Length
        dhtBytes[4] = dhtInfo //Huffman Table Info
        for (i in 0 until 16) {
            dhtBytes[5 + i] = huffmanTable.offset[i].toByte()
        }
        for (i in huffmanTable.symbols.indices) {
            dhtBytes[21 + i] = huffmanTable.symbols[i].toByte()
        }
        writeMarker(dhtBytes, outputStream)
    }

    fun writeSOS(sosSegment: SosSegment) {
        val byteSize = 4 + 1 + 3 + (sosSegment.numComponent * 2)
        val sosLength = byteSize - 2
        val sosBytes = ByteArray(byteSize)
        sosBytes[0] = 0xFF.toByte()
        sosBytes[1] = SOS
        sosBytes[2] = ((sosLength shr 8) and 0xFF).toByte()//Marker Length
        sosBytes[3] = (sosLength and 0xFF).toByte()//Marker Length
        sosBytes[4] = sosSegment.numComponent.toByte()//Number of channels/components in image
        var cIndex = 0
        while (cIndex < sosSegment.numComponent) {
            val component = sosSegment.components[cIndex]
            sosBytes[5 + (cIndex * 2)] = component.id.toByte()
            sosBytes[5 + (cIndex * 2) + 1] = packToByte(component.dcDhtTableId, component.acDhtTableId)
            cIndex++
        }
        sosBytes[5 + (cIndex * 2)] = sosSegment.startSelection.toByte()
        sosBytes[5 + (cIndex * 2) + 1] = sosSegment.endSelection.toByte()
        sosBytes[5 + (cIndex * 2) + 2] =
            packToByte(sosSegment.successiveApproximateHigh, sosSegment.successiveApproximateLow)
        writeMarker(sosBytes, outputStream)
    }

    fun encodeBlockComponent(
        bitWrite: BitWriter,
        block: IntArray,
        dcHuffmanTable: HuffmanEncoderTable,
        acHuffmanTable: HuffmanEncoderTable
    ) {
        if (block.isEmpty()) return
        val dcCoeff = block.first()
        val dcCoeffLength = dcCoeff.bitLength()
        val hDcCode = dcHuffmanTable.getCode(dcCoeffLength)

        if (hDcCode.isEmpty()) {
            throw Exception("Huffman Code Not Found in EncodeBlockComponent")
        }
        bitWrite.writeBits(hDcCode.toInt(2), hDcCode.length)
        bitWrite.writeBits(dcCoeff.toOneComplement(dcCoeffLength), dcCoeffLength)
        var i = 1
        while (i < 64) {
            var numZeroes = 0
            while (i < 64 && block[i] == 0) {
                numZeroes++
                i++
            }

            if (i == 64) {
                val hCode = acHuffmanTable.getCode(0)
                if (hCode.isEmpty()) {
                    throw Exception("Huffman Code Not Found in EncodeBlockComponent")
                }
                bitWrite.writeBits(hCode.toInt(2), hCode.length)
                return
            }

            while (numZeroes >= 16) {
                val hCode = acHuffmanTable.getCode(0xF0)
                if (hCode.isEmpty()) {
                    throw Exception("Huffman Code Not Found in EncodeBlockComponent")
                }
                bitWrite.writeBits(hCode.toInt(2), hCode.length)
                numZeroes -= 16
            }

            val acValue = block[i]
            val acLength = acValue.bitLength()
            if (acLength > 10) {
                throw Exception("AC coefficient length should not be greater than 10")
            }

            val symbolOne = packToInt(numZeroes, acLength)
            val hAcCode = acHuffmanTable.getCode(symbolOne)
            if (hAcCode.isEmpty()) {
                throw Exception("Huffman Code Not Found in EncodeBlockComponent = $symbolOne")
            }
            bitWrite.writeBits(hAcCode.toInt(2), hAcCode.length)
            bitWrite.writeBits(acValue.toOneComplement(acLength), acLength)
            i++
        }
    }

    fun writeComment(comment: String) {
        val commentLength = comment.length + 2
        val commentBytes = ByteArray(commentLength + 2)
        commentBytes[0] = 0xFF.toByte()
        commentBytes[1] = COM // Comment Marker
        commentBytes[2] = ((commentLength shr 8) and 0xFF).toByte()
        commentBytes[3] = (commentLength and 0xFF).toByte()
        for ((index, charComment) in comment.withIndex()) {
            commentBytes[index + 4] = charComment.code.toByte()
        }
        writeMarker(commentBytes, outputStream)
    }

    fun writeFooter() {
        // End Of Image (EOI) Marker
        val eoi = byteArrayOf(0xFF.toByte(), 0xD9.toByte())
        writeMarker(eoi, outputStream)
    }

    fun writeHuffmanBitstream(huffmanBitstream: List<Byte>) {
        writeMarker(huffmanBitstream.toByteArray(), outputStream)
    }

    companion object {
        private fun writeMarker(marker: ByteArray, imgStream: BufferedOutputStream) {
            try {
                imgStream.write(marker)
            } catch (e: IOException) {
                throw Exception("Image File Write Error: " + e.message)
            }
        }

        fun computeDcPrevious(blocks: List<ImageBlock>) {
            val dcPrev: MutableList<Int> = mutableListOf(0, 0, 0)

            var thisVal: Int
            for (cIndex in 0 until 3) {
                for (mcuIndex in blocks.indices) {
                    val currBlock = blocks[mcuIndex]
                    thisVal = currBlock.getC(cIndex)[0]
                    currBlock.getC(cIndex)[0] -= dcPrev[cIndex]
                    dcPrev[cIndex] = thisVal
                }
            }


        }

        fun processBlocks(bufferedImage: Bitmap, jpgHeader: JpgHeader) {
            val blocks = BitmapReader.getImageBlocks(bufferedImage)

            ColorConverter.rgbToYCbCr(blocks)

            val dct = Dct()
            dct.dctTransform(blocks, jpgHeader.mcuHeight, jpgHeader.mcuWidth)

            blocks.forEach {
                jpgHeader.dqtSegment.dqtTables[0]?.quantizeBlock(it.cOne)
                jpgHeader.dqtSegment.dqtTables[1]?.quantizeBlock(it.cTwo)
                jpgHeader.dqtSegment.dqtTables[1]?.quantizeBlock(it.cThree)
            }

            jpgHeader.blocks = blocks.toMutableList()

        }

        fun buildHuffmanTree(values: List<Int>, isAcTable: Boolean, isLuma: Boolean, skipBuild: Boolean = false): HuffmanEncoderTable {
            val valuesCounter = values.groupingBy { it }.eachCount().toList()

            val rootNode = HuffmanNodeWrite.encode(valuesCounter) ?: throw Exception("Building Huffman Tree Failed")

            return HuffmanEncoderTable.build(rootNode).let {
                it ?: when (Pair(isAcTable, isLuma)) {
                    // DC Chrominance Huffman
                    Pair(false, false) -> HuffmanEncoderTable(
                        intArrayOf(
                            0, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0
                        ),
                        mutableListOf(
                            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11
                        ),
                        mutableListOf(
                            "00", "01", "10", "110", "1110", "11110", "111110", "1111110", "11111110", "111111110", "1111111110", "11111111110"
                        )
                    )
                    // DC Luminance Huffman
                    Pair(false, true) -> HuffmanEncoderTable(
                        intArrayOf(
                            0, 1, 5, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0
                        ),
                        mutableListOf(
                            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11
                        ),
                        mutableListOf(
                            "00",
                            "010",
                            "011",
                            "100",
                            "101",
                            "110",
                            "1110",
                            "11110",
                            "111110",
                            "1111110",
                            "11111110",
                            "111111110",
                        )
                    )
                    // AC Chrominance Huffman
                    Pair(true, false) -> HuffmanEncoderTable(
                        intArrayOf(
                            0, 2, 1, 2, 4, 4, 3, 4, 7, 5, 4, 4, 0, 1, 2, 119
                        ),
                        mutableListOf(
                            0, 1, 2, 3, 17, 4, 5, 33, 49, 6, 18, 65, 81, 7, 97, 113, 19, 34, 50, 129, 8, 20, 66, 145, 161, 177, 193, 9, 35, 51, 82, 240, 21, 98, 114, 209, 10, 22, 36, 52, 225, 37, 241, 23, 24, 25, 26, 38, 39, 40, 41, 42, 53, 54, 55, 56, 57, 58, 67, 68, 69, 70, 71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90, 99, 100, 101, 102, 103, 104, 105, 106, 115, 116, 117, 118, 119, 120, 121, 122, 130, 131, 132, 133, 134, 135, 136, 137, 138, 146, 147, 148, 149, 150, 151, 152, 153, 154, 162, 163, 164, 165, 166, 167, 168, 169, 170, 178, 179, 180, 181, 182, 183, 184, 185, 186, 194, 195, 196, 197, 198, 199, 200, 201, 202, 210, 211, 212, 213, 214, 215, 216, 217, 218, 226, 227, 228, 229, 230, 231, 232, 233, 234, 242, 243, 244, 245, 246, 247, 248, 249, 250
                        ),
                        mutableListOf(
                            "00",
                            "01",
                            "100",
                            "1010",
                            "1011",
                            "11000",
                            "11001",
                            "11010",
                            "11011",
                            "111000",
                            "111001",
                            "111010",
                            "111011",
                            "1111000",
                            "1111001",
                            "1111010",
                            "11110110",
                            "11110111",
                            "11111000",
                            "11111001",
                            "111110100",
                            "111110101",
                            "111110110",
                            "111110111",
                            "111111000",
                            "111111001",
                            "111111010",
                            "1111110110",
                            "1111110111",
                            "1111111000",
                            "1111111001",
                            "1111111010",
                            "11111110110",
                            "11111110111",
                            "11111111000",
                            "11111111001",
                            "111111110100",
                            "111111110101",
                            "111111110110",
                            "111111110111",
                            "11111111100000",
                            "111111111000010",
                            "111111111000011",
                            "1111111110001000",
                            "1111111110001001",
                            "1111111110001010",
                            "1111111110001011",
                            "1111111110001100",
                            "1111111110001101",
                            "1111111110001110",
                            "1111111110001111",
                            "1111111110010000",
                            "1111111110010001",
                            "1111111110010010",
                            "1111111110010011",
                            "1111111110010100",
                            "1111111110010101",
                            "1111111110010110",
                            "1111111110010111",
                            "1111111110011000",
                            "1111111110011001",
                            "1111111110011010",
                            "1111111110011011",
                            "1111111110011100",
                            "1111111110011101",
                            "1111111110011110",
                            "1111111110011111",
                            "1111111110100000",
                            "1111111110100001",
                            "1111111110100010",
                            "1111111110100011",
                            "1111111110100100",
                            "1111111110100101",
                            "1111111110100110",
                            "1111111110100111",
                            "1111111110101000",
                            "1111111110101001",
                            "1111111110101010",
                            "1111111110101011",
                            "1111111110101100",
                            "1111111110101101",
                            "1111111110101110",
                            "1111111110101111",
                            "1111111110110000",
                            "1111111110110001",
                            "1111111110110010",
                            "1111111110110011",
                            "1111111110110100",
                            "1111111110110101",
                            "1111111110110110",
                            "1111111110110111",
                            "1111111110111000",
                            "1111111110111001",
                            "1111111110111010",
                            "1111111110111011",
                            "1111111110111100",
                            "1111111110111101",
                            "1111111110111110",
                            "1111111110111111",
                            "1111111111000000",
                            "1111111111000001",
                            "1111111111000010",
                            "1111111111000011",
                            "1111111111000100",
                            "1111111111000101",
                            "1111111111000110",
                            "1111111111000111",
                            "1111111111001000",
                            "1111111111001001",
                            "1111111111001010",
                            "1111111111001011",
                            "1111111111001100",
                            "1111111111001101",
                            "1111111111001110",
                            "1111111111001111",
                            "1111111111010000",
                            "1111111111010001",
                            "1111111111010010",
                            "1111111111010011",
                            "1111111111010100",
                            "1111111111010101",
                            "1111111111010110",
                            "1111111111010111",
                            "1111111111011000",
                            "1111111111011001",
                            "1111111111011010",
                            "1111111111011011",
                            "1111111111011100",
                            "1111111111011101",
                            "1111111111011110",
                            "1111111111011111",
                            "1111111111100000",
                            "1111111111100001",
                            "1111111111100010",
                            "1111111111100011",
                            "1111111111100100",
                            "1111111111100101",
                            "1111111111100110",
                            "1111111111100111",
                            "1111111111101000",
                            "1111111111101001",
                            "1111111111101010",
                            "1111111111101011",
                            "1111111111101100",
                            "1111111111101101",
                            "1111111111101110",
                            "1111111111101111",
                            "1111111111110000",
                            "1111111111110001",
                            "1111111111110010",
                            "1111111111110011",
                            "1111111111110100",
                            "1111111111110101",
                            "1111111111110110",
                            "1111111111110111",
                            "1111111111111000",
                            "1111111111111001",
                            "1111111111111010",
                            "1111111111111011",
                            "1111111111111100",
                            "1111111111111101",
                            "1111111111111110",
                        )
                    )
                    // AC Luminance Huffman
                    Pair(true, true) -> HuffmanEncoderTable(
                        intArrayOf(
                            0, 2, 1, 3, 3, 2, 4, 3, 5, 5, 4, 4, 0, 0, 1, 125
                        ),
                        mutableListOf(
                            1, 2, 3, 0, 4, 17, 5, 18, 33, 49, 65, 6, 19, 81, 97, 7, 34, 113, 20, 50, 129, 145, 161, 8, 35, 66, 177, 193, 21, 82, 209, 240, 36, 51, 98, 114, 130, 9, 10, 22, 23, 24, 25, 26, 37, 38, 39, 40, 41, 42, 52, 53, 54, 55, 56, 57, 58, 67, 68, 69, 70, 71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90, 99, 100, 101, 102, 103, 104, 105, 106, 115, 116, 117, 118, 119, 120, 121, 122, 131, 132, 133, 134, 135, 136, 137, 138, 146, 147, 148, 149, 150, 151, 152, 153, 154, 162, 163, 164, 165, 166, 167, 168, 169, 170, 178, 179, 180, 181, 182, 183, 184, 185, 186, 194, 195, 196, 197, 198, 199, 200, 201, 202, 210, 211, 212, 213, 214, 215, 216, 217, 218, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250
                        ),
                        mutableListOf("00", "01", "100", "1010", "1011", "1100", "11010", "11011", "11100", "111010", "111011", "1111000", "1111001", "1111010", "1111011", "11111000", "11111001", "11111010", "111110110", "111110111", "111111000", "111111001", "111111010", "1111110110", "1111110111", "1111111000", "1111111001", "1111111010", "11111110110", "11111110111", "11111111000", "11111111001", "111111110100", "111111110101", "111111110110", "111111110111", "111111111000000", "1111111110000010", "1111111110000011", "1111111110000100", "1111111110000101", "1111111110000110", "1111111110000111", "1111111110001000", "1111111110001001", "1111111110001010", "1111111110001011", "1111111110001100", "1111111110001101", "1111111110001110", "1111111110001111", "1111111110010000", "1111111110010001", "1111111110010010", "1111111110010011", "1111111110010100", "1111111110010101", "1111111110010110", "1111111110010111", "1111111110011000", "1111111110011001", "1111111110011010", "1111111110011011", "1111111110011100", "1111111110011101", "1111111110011110", "1111111110011111", "1111111110100000", "1111111110100001", "1111111110100010", "1111111110100011", "1111111110100100", "1111111110100101", "1111111110100110", "1111111110100111", "1111111110101000", "1111111110101001", "1111111110101010", "1111111110101011", "1111111110101100", "1111111110101101", "1111111110101110", "1111111110101111", "1111111110110000", "1111111110110001", "1111111110110010", "1111111110110011", "1111111110110100", "1111111110110101", "1111111110110110", "1111111110110111", "1111111110111000", "1111111110111001", "1111111110111010", "1111111110111011", "1111111110111100", "1111111110111101", "1111111110111110", "1111111110111111", "1111111111000000", "1111111111000001", "1111111111000010", "1111111111000011", "1111111111000100", "1111111111000101", "1111111111000110", "1111111111000111", "1111111111001000", "1111111111001001", "1111111111001010", "1111111111001011", "1111111111001100", "1111111111001101", "1111111111001110", "1111111111001111", "1111111111010000", "1111111111010001", "1111111111010010", "1111111111010011", "1111111111010100", "1111111111010101", "1111111111010110", "1111111111010111", "1111111111011000", "1111111111011001", "1111111111011010", "1111111111011011", "1111111111011100", "1111111111011101", "1111111111011110", "1111111111011111", "1111111111100000", "1111111111100001", "1111111111100010", "1111111111100011", "1111111111100100", "1111111111100101", "1111111111100110", "1111111111100111", "1111111111101000", "1111111111101001", "1111111111101010", "1111111111101011", "1111111111101100", "1111111111101101", "1111111111101110", "1111111111101111", "1111111111110000", "1111111111110001", "1111111111110010", "1111111111110011", "1111111111110100", "1111111111110101", "1111111111110110", "1111111111110111", "1111111111111000", "1111111111111001", "1111111111111010", "1111111111111011", "1111111111111100", "1111111111111101", "1111111111111110")
                    )

                    else -> HuffmanEncoderTable()
                }
            }
        }

        /**
         * This function takes an integer array (mcu) representing a Multi-Color Unit
         * and computes its Run-Length Encoding (RLE) representation.
         * RLE represents repeated values with a single value and a count.
         *
         * The function returns a mutable list of pairs. The first element in
         * the pair represents a combination of leading zeros and coefficient
         * bit length, and the second element represents the actual coefficient value.
         */
        fun computeRLE(mcu: IntArray): MutableList<Int> {
            //Coeff
            val result = mutableListOf(mcu.first().bitLength())

            var i = 1

            while (i < 64) {
                var leadingZero = 0
                while (i < 64 && mcu[i] == 0) {
                    leadingZero++
                    i++
                }

                if (i == 64) {
                    result.add(0)
                    return result
                }

                while (leadingZero >= 16) {
                    result.add(0xF0)
                    leadingZero -= 16
                }


                val valueLength = mcu[i].bitLength()
                result.add(packToInt(leadingZero, valueLength))
                i++
            }

            return result
        }

    }
}