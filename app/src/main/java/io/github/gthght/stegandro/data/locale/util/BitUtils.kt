package io.github.gthght.stegandro.data.locale.util

import kotlin.math.abs

/**
 * Returns the first four bits (half-byte) of the given integer.
 *
 * This function takes an integer as input and isolates the first four bits
 * (half-byte) by performing a bitwise right shift operation with the value `4`.
 * The resulting integer represents the first half-byte of the original input.
 *
 * @param value The integer from which to extract the first half-byte.
 * @return An integer representing the first four bits (half-byte) of the input.
 */
fun getFirstHalfByte(value: Int): Int {
    return value shr 4
}

/**
 * Returns the last four bits (half-byte) of the given integer.
 *
 * This function takes an integer as input and isolates the last four bits
 * (half-byte) by performing a bitwise AND operation with the hexadecimal value `0x0F`.
 * The resulting integer represents the last half-byte of the original input.
 *
 * @param value The integer from which to extract the last half-byte.
 * @return An integer representing the last four bits (half-byte) of the input.
 */
fun getLastHalfByte(value: Int): Int {
    return value and 0x0F
}

/**
 * Calculates the number of bits required to represent the integer in binary form.
 *
 * @return The number of bits required to represent the integer.
 */

fun Int.bitLength(): Int =
    32 - (abs(this).countLeadingZeroBits())

/**
 * Converts a boolean value to an integer (0 for false, 1 for true).
 *
 * @return 1 if the boolean is true, 0 otherwise.
 */
fun Boolean.toInt() = if (this) 1 else 0


/**
 * Packs two integer halves (each 4 bits) into a single byte.
 *
 * This function takes two integer values, each representing the lower 4 bits (half byte) of the desired byte.
 * It performs a bitwise AND operation with 0xF (1111 in binary) to isolate the lower 4 bits of each half.
 * Then, it shifts the first half 4 bits to the left (shl) and combines it with the second half using a bitwise OR operation.
 * Finally, the result is converted to a byte.
 *
 * @param firstHalf The first integer half (lower 4 bits).
 * @param lastHalf The second integer half (lower 4 bits).
 * @return A single byte created by combining the two halves.
 */
fun packToByte(firstHalf: Int, lastHalf: Int): Byte {
    return (((firstHalf and 0xF) shl 4) + (lastHalf and 0xF)).toByte()
}

/**
 * Packs two integer halves (each 4 bits) into a single integer.
 *
 * This function is similar to `packToByte` but combines the two halves into an integer instead of a byte.
 * It follows the same logic of isolating the lower 4 bits of each half, shifting the first half 4 bits left, and combining them with a bitwise OR operation.
 *
 * @param firstHalf The first integer half (lower 4 bits).
 * @param lastHalf The second integer half (lower 4 bits).
 * @return A single integer created by combining the two halves.
 */
fun packToInt(firstHalf: Int, lastHalf: Int): Int = ((firstHalf and 0xF) shl 4) + (lastHalf and 0xF)

/**
 * This is not necessarily a one's complement conversion, but conversion required for
 * JPEG Entropy Coding symbol2 because symbol2 wasn't supposed to have a negative integer.
 * If symbol2 is negative it needed to be converted in this way.
 *
 * @param bitLength The total number of bits representing the integer.
 * @return The negative symbol representation of the integer.
 */
fun Int.toOneComplement(bitLength: Int): Int {
    return if (this < 0) {
        this + ((1 shl bitLength) - 1)
    } else {
        this
    }
}

/**
 * Sets the least significant bit (LSB) of the integer to a specified value.
 *
 * @param value The value to set for the LSB (0 or 1).
 * @return The integer with the LSB set to the specified value.
 */
fun Int.setLsb(value: Int): Int {
    return if (this > 0) {
        ((this shr 1) shl 1) or (value and 1)
    } else {
        -((abs(this) and 1.inv()) or value)
    }
}

/**
 * Extracts the least significant bit (LSB) of the integer.
 *
 * @return The value of the least significant bit (0 or 1).
 */
fun Int.getLsb(): Int =
    abs(this) % 2

/**
 * Extract the value of a bit at a given position (index) within the character's underlying binary code.
 *
 * @param position The zero-based index of the bit to check (lsb is 0)
 * @return The bit at the specified position
 */
fun Char.getBit(position: Int): Int =
    (this.code shr position) and 1

fun incrementBinaryString(binaryString: String): String {
    val reversed = binaryString.reversed()
    var carry = 1
    val resultList = mutableListOf<Char>()

    for (char in reversed) {
        val digit = char - '0' + carry
        resultList.add((digit % 2).toString().single())
        carry = digit / 2
    }

    return if (carry == 0) resultList.reversed().joinToString("") else "1" + resultList.reversed()
        .joinToString("")
}
