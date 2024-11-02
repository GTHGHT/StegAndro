package io.github.gthght.stegandro.data.locale.jpg.process.subprocess


import io.github.gthght.stegandro.data.locale.jpg.data.huffman.HuffmanNode
import io.github.gthght.stegandro.data.locale.jpg.data.huffman.HuffmanTree
import io.github.gthght.stegandro.data.locale.jpg.data.segment.subsegment.SosComponent
import io.github.gthght.stegandro.data.locale.jpg.data.structure.ImageBlock
import io.github.gthght.stegandro.data.locale.util.getFirstHalfByte
import io.github.gthght.stegandro.data.locale.util.getLastHalfByte
import io.github.gthght.stegandro.data.locale.util.zigZagMap
import java.io.InputStream
import java.util.Arrays

class HuffmanDecoder(
    inputStream: InputStream,
    private val dcTables: List<HuffmanTree>,
    private val acTables: List<HuffmanTree>
) {
    constructor(
        inputStream: InputStream,
        dcTables: Array<HuffmanTree?>,
        acTables: Array<HuffmanTree?>
    ) : this(
        inputStream,
        dcTables.filterNotNull().requireNoNulls(),
        acTables.filterNotNull().requireNoNulls()
    )

    //will be used for reusage
    private val block: IntArray = IntArray(8 * 8)
    private val bitStream: BitInputStream = BitInputStream(inputStream)

    /**
     * Decodes input byte array producing 1 MCU for call.
     * Throws IllegalStateException when end of input is reached
     *
     */
    fun decode(holder: ImageBlock, dcPrevs: IntArray, sosComponent: SosComponent): IntArray {
        val mcu = decode(dcTables[sosComponent.dcDhtTableId], acTables[sosComponent.acDhtTableId])
        mcu[0] += dcPrevs[sosComponent.id - 1]
        holder.replaceMCU(sosComponent.id - 1, mcu)
        dcPrevs[sosComponent.id - 1] = mcu[0]
        return dcPrevs
    }

    /**
     * Decodes single 8x8 array either Y, either Cr, either Cb.
     */
    private fun decode(dcTable: HuffmanTree, acTable: HuffmanTree): IntArray {
        Arrays.fill(block, 0)
        block[0] = decodeDC(dcTable)
        decodeAC(acTable)
        return block
    }

    /**
     * Finds huffman code in huffman tree. Algorithm is pretty simple :
     * - Read 1 bit;
     * - go down (to left node in case read bit is 0 and to right node in case read bit is 1) through huffman tree starting from root;
     * - if current node has filled code (code > -1) we found huffman code. otherwise - repeat.
     *
     */
    private fun findCode(table: HuffmanTree): Int {
        var start: HuffmanNode? = table.root

        for (counter in 0..15) {
            val i: Int = bitStream.nextBit()
            start = if (i == 1) start!!.node1 else start!!.node0

            if (start!!.symbol > -1) {
                return start.symbol
            }
        }
        return findCode(table)
    }

    /**
     * Decodes single DC value. Algorithm is next :
     * - find huffman code for current bit position
     * -
     * a) if code is 0 return 0
     * b) if code starts from 1 return code itself
     * c) if code starts from 0 calculate returned value by: code - 2^length_in_bits_of_code + 1
     *
     */
    private fun decodeDC(dcTable: HuffmanTree): Int {
        val value = findCode(dcTable)
        return decodeCode(bitStream, value)
    }

    /**
     * Decodes rest of 63 AC values
     */
    private fun decodeAC(acTable: HuffmanTree) {
        var k = 1
        while (k < 8 * 8) {
            val code = findCode(acTable)
            val zerosNumber: Int = getFirstHalfByte(code)
            val bitCount: Int = getLastHalfByte(code)

            if (bitCount != 0) {
                k += zerosNumber
                block[zigZagMap[k]] = decodeCode(bitStream, bitCount)
            } else {
                if (zerosNumber != 0x0F) {
                    return
                }
                k += 0x0F
            }
            k++
        }
    }

    fun align() {
        bitStream.align()
    }

    companion object {
        private fun decodeCode(bits: BitInputStream, bitCount: Int): Int {
            if (bitCount == 0) return 0

            val firstBit: Int = bits.nextBit()
            var value = 0
            value = (value shl 1) + firstBit

            for (i in 1 until bitCount) {
                value = (value shl 1) + bits.nextBit()
            }

            // Check if the value is a negative number or a positive value
            return if (firstBit == 1) {
                value
            } else {
                value - (1 shl bitCount) + 1
            }
        }
    }
}
