package io.github.gthght.stegandro.data.locale.jpg.data.segment

import io.github.gthght.stegandro.data.locale.jpg.data.huffman.HuffmanNode
import io.github.gthght.stegandro.data.locale.jpg.data.huffman.HuffmanTree
import io.github.gthght.stegandro.data.locale.jpg.data.segment.subsegment.DhtTable
import io.github.gthght.stegandro.data.locale.util.getFirstHalfByte
import io.github.gthght.stegandro.data.locale.util.getLastHalfByte
import java.io.InputStream

class DhtSegment {
    // For baseline the max is 2, but for extended sequential the max is 4
    val dcTables: Array<HuffmanTree?> = arrayOfNulls(4)
    val acTables: Array<HuffmanTree?> = arrayOfNulls(4)

    fun add(dhtTable: DhtTable) {
        if (!dhtTable.isAcTable) {
            dcTables[dhtTable.id] = dhtTable.getTree()
        } else {
            acTables[dhtTable.id] = dhtTable.getTree()
        }
    }

    fun add(dhtTables: List<DhtTable>) {
        dhtTables.forEach {
            add(it)
        }
    }

    fun printInfo() {
        println("DHT Segment:")
        println("DC DHT Tables=")
        printTables(dcTables)
        println("AC DHT Tables=")
        printTables(acTables)
    }

    private fun printTables(tables: Array<HuffmanTree?>) {
        for (i in tables.indices) {
            val currentTree = dcTables[i]
            if (currentTree != null) {
                println("Huffman Tree Index $i")
                println("Total Level: ${currentTree.currentBranch.level()}")
                HuffmanNode.printCode(currentTree.root)
            }
        }
    }


    companion object {
        fun decode(inputStream: InputStream): List<DhtTable> {
            var length = (inputStream.read() shl 8) + inputStream.read()
            length -= 2

            val dhtList: MutableList<DhtTable> = mutableListOf()
            while (length > 0) {
                val tableInfo = inputStream.read()
                val isAcTable = getFirstHalfByte(tableInfo) == 1
                val tableId = getLastHalfByte(tableInfo)

                if (tableId > 3) {
                    throw Exception("Huffman Table ID is invalid")
                }

                val currOffset = mutableListOf(0)

                for (i in 1..16) {
                    currOffset.add(currOffset[i - 1] + inputStream.read())
                }

                if (currOffset.last() > 162) {
                    throw Exception("Huffman Tables Has Too Many Symbols")
                }

                val huffmanTree = HuffmanTree()
                for (i in 0 until 16) {
                    for (j in currOffset[i] until currOffset[i + 1]) {
                        val code: Int = inputStream.read()
                        huffmanTree.addLeaf(code)
                    }
                    if (i < currOffset.size - 1) {
                        huffmanTree.fillLevel()
                    }
                }

                DhtTable(tableId, huffmanTree, isAcTable).let {
                    dhtList.add(it)
                }
                length -= 17 + currOffset.last()
            }

            return dhtList
        }
    }
}
