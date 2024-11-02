package io.github.gthght.stegandro.data.locale.util

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sqrt

/**
 * This contains the list of JPG Marker required for the encoders and decoders
 *
 * **See Also:** [List Of JPG Markers](https://github.com/corkami/formats/blob/master/image/jpeg.md)
 */

// APPN Marker
const val APP0: Byte = 0xE0.toByte()
const val APP1: Byte = 0xE1.toByte() // (EXIF/XMP/XAP)
const val APP2: Byte = 0xE2.toByte() // (FlashPix / ICC)
const val APP3: Byte = 0xE3.toByte()
const val APP4: Byte = 0xE4.toByte()
const val APP5: Byte = 0xE5.toByte()
const val APP6: Byte = 0xE6.toByte() // (GoPro...)
const val APP7: Byte = 0xE7.toByte() // (Pentax/Qualcomm)
const val APP8: Byte = 0xE8.toByte()
const val APP9: Byte = 0xE9.toByte()
const val APP10: Byte = 0xEA.toByte()
const val APP11: Byte = 0xEB.toByte()
const val APP12: Byte = 0xEC.toByte() // (photoshoP ducky / savE foR web)
const val APP13: Byte = 0xED.toByte() // (photoshoP savE As)
const val APP14: Byte = 0xEE.toByte() // ("adobe" (length = 12))
const val APP15: Byte = 0xEF.toByte() // (GraphicConverter)

// Restart (Interval) Marker
const val RST0: Byte = 0xD0.toByte()
const val RST1: Byte = 0xD1.toByte()
const val RST2: Byte = 0xD2.toByte()
const val RST3: Byte = 0xD3.toByte()
const val RST4: Byte = 0xD4.toByte()
const val RST5: Byte = 0xD5.toByte()
const val RST6: Byte = 0xD6.toByte()
const val RST7: Byte = 0xD7.toByte()

const val SOF0: Byte = 0xC0.toByte() // Baseline process.DCT
const val SOS: Byte = 0xDA.toByte() // Start Of Scan
const val SOI: Byte = 0xD8.toByte() // Start Of Image
const val EOI: Byte = 0xD9.toByte() // End Of Image

const val COM: Byte = 0xFE.toByte() // Comment Marker

const val DRI: Byte = 0xDD.toByte() // Define Restart Interval
const val DQT: Byte = 0xDB.toByte() // Define Quantization Table
const val DHT: Byte = 0xC4.toByte() // Define Huffman Table


//Currently Unsupported Marker
const val DAC: Byte = 0xCC.toByte() // Define Arithmetic Coding
const val DNL: Byte = 0xDC.toByte() // Define Number Of Lines
const val DHP: Byte = 0xDE.toByte() // Define Hierarchical Progression
const val EXP: Byte = 0xDF.toByte() // Expand Reference Components
const val TEM: Byte = 0x01.toByte() // TEM temporary marker for arithmetic coding

//Reserved For Future Marker
const val JPG0: Byte = 0xF0.toByte()
const val JPG1: Byte = 0xF1.toByte()
const val JPG2: Byte = 0xF2.toByte()
const val JPG3: Byte = 0xF3.toByte()
const val JPG4: Byte = 0xF4.toByte()
const val JPG5: Byte = 0xF5.toByte()
const val JPG6: Byte = 0xF6.toByte()
const val JPG7: Byte = 0xF7.toByte() // JPEG-Lossless SOF48 start of frame
const val JPG8: Byte = 0xF8.toByte() // JPEG-Lossless LSE extension parameters
const val JPG9: Byte = 0xF9.toByte()
const val JPG10: Byte = 0xFA.toByte()
const val JPG11: Byte = 0xFB.toByte()
const val JPG12: Byte = 0xFC.toByte()
const val JPG13: Byte = 0xFD.toByte()

val zigZagMap: IntArray = intArrayOf(
    0,   1,  8, 16,  9,  2,  3, 10,
    17, 24, 32, 25, 18, 11,  4,  5,
    12, 19, 26, 33, 40, 48, 41, 34,
    27, 20, 13,  6,  7, 14, 21, 28,
    35, 42, 49, 56, 57, 50, 43, 36,
    29, 22, 15, 23, 30, 37, 44, 51,
    58, 59, 52, 45, 38, 31, 39, 46,
    53, 60, 61, 54, 47, 55, 62, 63
)

val reverseZigzagMap = listOf(
    0, 1, 5, 6, 14, 15, 27, 28, 2, 4,
    7, 13, 16, 26, 29, 42, 3, 8, 12,
    17, 25, 30, 41, 43, 9, 11, 18,
    24, 31, 40, 44, 53, 10, 19, 23,
    32, 39, 45, 52, 54, 20, 22, 33,
    38, 46, 51, 55, 60, 21, 34, 37,
    47, 50, 56, 59, 61, 35, 36, 48,
    49, 57, 58, 62, 63)

// Base Luminance Quantization Table
val luminanceQT = intArrayOf(
    8, 8, 8, 8, 9, 9, 11, 12,
    8, 8, 8, 8, 9, 10, 11, 13,
    8, 8, 9, 9, 10, 11, 13, 15,
    8, 8, 9, 11, 12, 14, 16, 18,
    9, 9, 10, 12, 15, 18, 21, 24,
    9, 10, 11, 14, 18, 22, 27, 33,
    11, 11, 13, 16, 21, 27, 35, 44,
    12, 13, 15, 18, 24, 33, 44, 58
)

val luminanceLowQT = intArrayOf(
    2, 2, 2, 3, 5, 8, 10, 12,
    2, 2, 3, 4, 5, 12, 12, 11,
    3, 3, 3, 5, 8, 11, 14, 11,
    3, 3, 4, 6, 10, 17, 16, 12,
    4, 4, 7, 11, 14, 22, 21, 15,
    5, 7, 11, 13, 16, 21, 23, 18,
    10, 13, 16, 17, 21, 24, 24, 20,
    14, 18, 19, 20, 22, 20, 21, 20
)

// Base Chrominance Quantization Table
val chrominanceQT = intArrayOf(
    3, 7, 10, 19, 40, 40, 40, 40,
    7, 8, 10, 26, 40, 40, 40, 40,
    10, 10, 22, 40, 40, 40, 40, 40,
    19, 26, 40, 40, 40, 40, 40, 40,
    40, 40, 40, 40, 40, 40, 40, 40,
    40, 40, 40, 40, 40, 40, 40, 40,
    40, 40, 40, 40, 40, 40, 40, 40,
    40, 40, 40, 40, 40, 40, 40, 40
)

val chrominanceLowQT = intArrayOf(
    3, 4, 5, 9, 20, 20, 20, 20,
    4, 4, 5, 13, 20, 20, 20, 20,
    5, 5, 11, 20, 20, 20, 20, 20,
    9, 13, 20, 20, 20, 20, 20, 20,
    20, 20, 20, 20, 20, 20, 20, 20,
    20, 20, 20, 20, 20, 20, 20, 20,
    20, 20, 20, 20, 20, 20, 20, 20,
    20, 20, 20, 20, 20, 20, 20, 20
)

val oneQt = intArrayOf(
    1, 1, 1, 1, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1
)

val m0 = 2.0 * cos(1.0 / 16.0 * 2.0 * PI)
val m1 = 2.0 * cos(2.0 / 16.0 * 2.0 * PI)
val m3 = 2.0 * cos(2.0 / 16.0 * 2.0 * PI)
val m5 = 2.0 * cos(3.0 / 16.0 * 2.0 * PI)
val m2 = m0 - m5
val m4 = m0 + m5

val s0 = cos(0.0 / 16.0 * PI) / sqrt(8.0)
val s1 = cos(1.0 / 16.0 * PI) / 2.0
val s2 = cos(2.0 / 16.0 * PI) / 2.0
val s3 = cos(3.0 / 16.0 * PI) / 2.0
val s4 = cos(4.0 / 16.0 * PI) / 2.0
val s5 = cos(5.0 / 16.0 * PI) / 2.0
val s6 = cos(6.0 / 16.0 * PI) / 2.0
val s7 = cos(7.0 / 16.0 * PI) / 2.0
