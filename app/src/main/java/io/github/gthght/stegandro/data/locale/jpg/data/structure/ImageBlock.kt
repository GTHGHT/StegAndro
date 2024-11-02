package io.github.gthght.stegandro.data.locale.jpg.data.structure

import java.util.Locale

class ImageBlock(
    val cOne: IntArray = IntArray(64),
    val cTwo: IntArray = IntArray(64),
    val cThree: IntArray = IntArray(64),
    var isCOneOnly: Boolean = false
) {
    fun replaceMCU(colorIndex: Int, mcu: IntArray){
        when(colorIndex){
            0 -> cOne
            1 -> cTwo
            else -> cThree
        }.let { chosenC ->
            mcu.copyInto(chosenC)
        }
    }

    fun getC (index:Int): IntArray {
        return when(index){
            0-> cOne
            1-> cTwo
            else -> cThree
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("ImageBlock(\n")
        sb.append(cOne.contentToString())
        sb.append("\n")
        sb.append(cTwo.contentToString())
        sb.append("\n")
        sb.append(cThree.contentToString())
        sb.append("\n) ")
        return sb.toString()
    }

    fun printComponents() {
        val tableHeader = "Index  | cOne  | cTwo  | cThree\n"
        val separator = "-".repeat(48)

        println(tableHeader + separator)

        // Loop through each index (0 to 63)
        for (i in 0..63) {
            val indexStr = String.format(Locale.getDefault(), "%3d", i) // Pad index with spaces for alignment
            val cOneValue = cOne[i]
            val cTwoValue = cTwo[i]
            val cThreeValue = cThree[i]
            val row = String.format("$indexStr  | %4d  | %4d  | %4d", cOneValue, cTwoValue, cThreeValue)
            println(row)
        }
    }
}