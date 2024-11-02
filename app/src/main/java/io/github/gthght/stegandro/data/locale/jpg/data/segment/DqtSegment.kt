package io.github.gthght.stegandro.data.locale.jpg.data.segment

import io.github.gthght.stegandro.data.locale.jpg.data.segment.subsegment.DqtTable
import io.github.gthght.stegandro.data.locale.util.getFirstHalfByte
import io.github.gthght.stegandro.data.locale.util.getLastHalfByte
import io.github.gthght.stegandro.data.locale.util.zigZagMap
import java.io.InputStream

class DqtSegment {
    val dqtTables: Array<DqtTable?> = arrayOfNulls(3)

    fun add(dqtList: List<DqtTable>){
        dqtList.forEach{
            add(it)
        }
    }

    fun add(dqtTable: DqtTable) {
        dqtTables[dqtTable.id] = dqtTable
    }

    fun printInfo(){
        println("DQT Segment: ")
        dqtTables.forEach {
            if (it != null){
                println("ID: ${it.id}, Precision: ${it.precision}")
                println(it.data.contentToString())
            }
        }
    }

    companion object {
        fun decode(inputStream: InputStream): List<DqtTable> {
            var length: Int = (inputStream.read() shl 8) + inputStream.read()
            length -= 2

            val result: MutableList<DqtTable> = mutableListOf()
            while(length > 0){
                val qtInfo: Int = inputStream.read()
                length -=1
                val precision: Int = getFirstHalfByte(qtInfo)
                val qtId: Int = getLastHalfByte(qtInfo)

                if (qtId > 3) {
                    throw Exception("Quantization Table is invalid, Table ID = $qtId")
                }

                val qTable = DqtTable(qtId, precision= precision)
                result.add(qTable)
                if (qTable.precision == 1) {
                    for (i in 0..<64) {
                        qTable.data[zigZagMap[i]] = (inputStream.read() shl 8) + inputStream.read()
                    }
                    length -= 128
                } else {
                    for (i in 0..<64) {
                        qTable.data[zigZagMap[i]] = inputStream.read()
                    }
                    length -= 64
                }
            }
            return result
        }
    }
}
