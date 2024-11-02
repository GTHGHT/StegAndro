package io.github.gthght.stegandro.data.locale.crypto

import kotlin.math.ceil

/**
 * MyszkowskiCipher is a class that provides methods for encrypting and decrypting text using
 * the Myszkowski Transposition Cipher algorithm. This algorithm involves rearranging the characters
 * of the text based on a given key, with optional null padding for encryption.
 */
class MyszkowskiCipher {

    /**
     * Encrypts the provided plain text using the Myszkowski Transposition Cipher algorithm.
     *
     * @param plainText The text to be encrypted.
     * @param key The key used for rearranging the characters during encryption.
     * @return The encrypted text.
     */
    fun encrypt(plainText: String, key: String): String {
        val columns: List<MutableList<Char>> = List(key.length) {
            mutableListOf()
        }

        val keyIndex = generateKeyIndex(key)

        var colIndex = 0
        for (element in plainText) {
            columns[colIndex].add(element)
            colIndex = (colIndex + 1) % key.length
        }

        val resultBuilder = StringBuilder()
        colIndex = 0
        while (colIndex < keyIndex.size) {
            val numDuplicates = countDuplicates(keyIndex[colIndex].second, keyIndex)
            if (numDuplicates > 1) {
                interleaveColumns(
                    resultBuilder,
                    keyIndex.filterIndexed { index, _ ->
                        colIndex <= index && index < colIndex + numDuplicates
                    }.map { columns[it.first] }
                )
                colIndex += numDuplicates
                continue
            }
            resultBuilder.append(columns[keyIndex[colIndex].first].joinToString(""))
            colIndex++
        }

        return resultBuilder.toString()
    }

    /**
     * Decrypts the provided cipher text using the Myszkowski Transposition Cipher algorithm.
     *
     * @param cipherText The text to be decrypted.
     * @param key The key used for rearranging the characters during decryption.
     * @return The decrypted text.
     */
    fun decrypt(cipherText: String, key: String): String {
        val columns: List<MutableList<Char>> = List(key.length) {
            mutableListOf()
        }

        val keyIndex = generateKeyIndex(key)

        val numRows: Int = ceil(cipherText.length.toDouble() / key.length).toInt()

        val numNulls: Int = (numRows * key.length) - cipherText.length

        var cipherIndex = 0
        var indexPair = 0
        while (indexPair < keyIndex.size) {
            val duplicates = keyIndex.filter { it.second == keyIndex[indexPair].second }
            if (duplicates.size > 1) {
                repeat(numRows) { rowIndex ->
                    for (pairIndex in duplicates) {
                        if (rowIndex < numRows - 1 || (key.length - numNulls) > pairIndex.first) {
                            columns[pairIndex.first].add(cipherText[cipherIndex++])
                        }
                    }
                }
                indexPair += duplicates.size
                continue
            } else {
                repeat(numRows) {rowIndex ->
                    val columnIndex = keyIndex[indexPair].first
                    if (columnIndex < key.length - numNulls || rowIndex < numRows-1) {
                        columns[columnIndex]
                            .add(cipherText[cipherIndex++])
                    }
                }
                indexPair++
            }
        }

        val resultBuilder = StringBuilder()
        for (row in 0 until numRows) {
            repeat(key.length) { colIndex ->
                columns[colIndex].let {
                    if (row < it.size) {
                        resultBuilder.append(it[row])
                    }
                }
            }
        }

        return resultBuilder.toString()
    }


    companion object {
        /**
         * Generates a list of pairs representing the index of characters in the original key
         * along with their corresponding column order based on character sorting.
         *
         * @param key The input key string.
         * @return A list of pairs where the first element is the original index of the character
         *         in the key, and the second element is the column order based on character sorting.
         */
        fun generateKeyIndex(key: String): List<Pair<Int, Int>> {
            val keySorted = key.toList().zip(key.indices).sortedBy { it.first }
            var columnOrder = 0

            return keySorted.mapIndexed { index, (char, originalIndex) ->
                val order = if (index > 0 && keySorted[index - 1].first == char) {
                    columnOrder
                } else {
                    ++columnOrder
                }

                Pair(originalIndex, order)
            }
        }

        /**
         * Counts the occurrences of a specified column order value in the list of key indices.
         *
         * @param value The column order value for which duplicates are counted.
         * @param keyIndex The list of pairs representing the original index and column order
         *                 of characters in the key.
         * @return The number of occurrences of the specified column order value in the key indices.
         */
        fun countDuplicates(value: Int, keyIndex: List<Pair<Int, Int>>): Int {
            return keyIndex.count { it.second == value }
        }

        /**
         * Interleaves the characters from the given columns and appends the result to a StringBuilder.
         *
         * @param stringBuilder The StringBuilder to which the interleaved characters will be appended.
         * @param columns A list of mutable lists representing columns of characters to be interleaved.
         */
        fun interleaveColumns(stringBuilder: StringBuilder, columns: List<MutableList<Char>>) {
            for (i in 0 until columns[0].size) {
                for (element in columns) {
                    if (i < element.size) {
                        stringBuilder.append(element[i])
                    }
                }
            }
        }
    }
}