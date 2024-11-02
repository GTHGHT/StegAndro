package io.github.gthght.stegandro.data.locale.jpg.data.huffman

import io.github.gthght.stegandro.data.locale.util.incrementBinaryString


class HuffmanEncoderTable(
    val offset: IntArray = IntArray(16),
    val symbols: MutableList<Int> = mutableListOf(),
    private val codes: MutableList<String> = mutableListOf()
) {
    fun getCode(symbol: Int): String =
        symbols.indexOf(symbol).let {
            if (it == -1) "" else codes[it]
        }

    fun printInfo() {
        for (i in symbols.indices) {
            println("${symbols[i]}:${codes[i]} | ${codes[i].length}")
        }
    }

    companion object {

        fun build(parentNode: HuffmanNode): HuffmanEncoderTable? {
            try {
                //Pair(code, huffmanCodeLength)
                val codeLengthList: MutableList<Pair<Int, Int>> = mutableListOf()

                if (parentNode.isLeaf) {
                    return HuffmanEncoderTable(
                        IntArray(16) {
                            if (it == 0) 1 else 0
                        },
                        mutableListOf(parentNode.symbol),
                        mutableListOf("0")
                    )
                }

                val stack = ArrayDeque<HuffmanNode>()
                stack.addLast(parentNode)

                val offset = IntArray(32) { 0 }
                val symbols: MutableList<Int> = mutableListOf()
                while (stack.isNotEmpty()) {
                    val currentNode = stack.removeFirst()
                    if (currentNode.isLeaf) {
                        val codeLength = currentNode.level()
                        if (codeLength > 32) throw Exception("Huffman Code Exceed 32 bit $codeLength")
                        codeLengthList.add(Pair(currentNode.symbol, codeLength))
                        offset[codeLength - 1] += 1
                        symbols.add(currentNode.symbol)
                    } else {
                        currentNode.node1?.let { stack.addLast(it) }
                        currentNode.node0?.let { stack.addLast(it) }
                    }
                }
                if (symbols.size > 162) {
                    throw IndexOutOfBoundsException("Symbols Exceeded The 162 Symbol Limit")
                }

                optimizeOffset(offset)

                val codes: MutableList<String> = mutableListOf()

                var code = "0"
                var currentCodeLength = 1
                var total: Int = offset[0]
                var arrayIndex = 1
                for (i in codeLengthList.indices) {
                    while (total < i + 1) {
                        total += offset[arrayIndex++]
                    }

                    val hCodeLength = arrayIndex
                    while (currentCodeLength < hCodeLength) {
                        code += "0"
                        currentCodeLength++
                    }
                    codes.add(code)
                    code = incrementBinaryString(code)

                    currentCodeLength = hCodeLength
                }
                return HuffmanEncoderTable(offset.copyOf(16), symbols, codes)
            } catch (e: IndexOutOfBoundsException) {
                return null
            }
        }

        private fun optimizeOffset(bits: IntArray) {
            var i = 31
            var j: Int
            var isShifting = false
            while (true) {
                if (bits[i] > 0) {
                    isShifting = true
                    j = i - 1
                    j--
                    while (bits[j] <= 0) {
                        j--
                    }
                    if (bits[i] >= 2) {
                        bits[i] -= 2
                        bits[i - 1] += 1
                        bits[j + 1] += 2
                        bits[j] -= 1
                    } else {
                        bits[i] -= 1
                        bits[i - 1] += 1
                        bits[j + 1] += 2
                        bits[j] -= 1
                    }
                    continue
                } else {
                    i--
                    if (i != 15) {
                        continue
                    }
                    while (bits[i] == 0) {
                        i--
                    }
                    if (isShifting) {
                        bits[i] -= 1
                        isShifting = false
                    }
                    return
                }
            }
        }
    }

}