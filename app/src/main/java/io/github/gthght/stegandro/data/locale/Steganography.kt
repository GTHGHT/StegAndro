package io.github.gthght.stegandro.data.locale

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import io.github.gthght.stegandro.data.locale.crypto.AutokeyCipher
import io.github.gthght.stegandro.data.locale.crypto.MyszkowskiCipher
import io.github.gthght.stegandro.data.locale.jpg.data.segment.DqtSegment
import io.github.gthght.stegandro.data.locale.jpg.data.structure.JpgHeader
import io.github.gthght.stegandro.data.locale.jpg.process.JpgDecoder
import io.github.gthght.stegandro.data.locale.jpg.process.JpgEncoder
import io.github.gthght.stegandro.data.locale.jpg.process.subprocess.BitWriter
import io.github.gthght.stegandro.data.locale.jpg.process.subprocess.HuffmanDecoder
import io.github.gthght.stegandro.data.locale.jpg.process.subprocess.ZigzagScanner
import io.github.gthght.stegandro.data.locale.util.DQT
import io.github.gthght.stegandro.data.locale.util.ImageMetrics
import io.github.gthght.stegandro.data.locale.util.SOS
import io.github.gthght.stegandro.data.locale.util.getBit
import io.github.gthght.stegandro.data.locale.util.getLsb
import io.github.gthght.stegandro.data.locale.util.setLsb
import io.github.gthght.stegandro.data.repository.ImageMetricsResult
import io.github.gthght.stegandro.util.getBitmap
import io.github.gthght.stegandro.util.getImageUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject


class Steganography @Inject constructor(private val context: Context) : SteganographyInterface {

    /**
     * Calculates the total payload capacity in bits for hiding data within a JPEG image.
     *
     * This function analyzes the image data to count the number of coefficient that embedding
     * can occur. It iterates through image blocks and checks if the coefficient in it is
     * greater than 1 or less than -1 (excluding 1, 0, and -1).
     *
     * @param uri The image file path to analyze.
     * @return The total payload capacity in byte for data hiding.
     *
     * @throws IOException If an error occurs while reading the image file.
     */

    override suspend fun checkPayloadCapacity(
        uri: Uri,
        isSampled: Boolean
    ): Int {
        uri.path?.let {
            val contentResolver = context.contentResolver
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            if (inputStream != null) {
                val fileExtension = contentResolver.getType(uri)
                val isJpg = fileExtension == "image/jpeg"
                val bitmapImage = getBitmap(contentResolver, uri, isSampled)
                val header = JpgHeader.build(bitmapImage)
                if (isJpg) {
                    readDQT(inputStream, header)
                }
                withContext(Dispatchers.IO) {
                    inputStream.close()
                }
                JpgEncoder.processBlocks(bitmapImage, header)

                // Total payload capacity in bit(s)
                var payloadCapacity = 0
                header.blocks.forEach { imageBlock ->
                    for (component in 0 until 3) {
                        for (i in 63 downTo 4) {
                            if (i in listOf(8, 9, 10, 16, 17, 24)) {
                                continue
                            }
                            val coeff = imageBlock.getC(component)[i]
                            if (coeff > 1 || coeff < -1) payloadCapacity++
                        }
                    }
                }

                return payloadCapacity / 8
            } else {
                throw FileNotFoundException("idak Dapat Membaca Gambar")
            }
        }
        return -1
    }

    private fun readDQT(inputStream: InputStream, jpgHeader: JpgHeader) {
        var isReading = true
        var numOfTableRead = 0

        while (isReading) {
            val data = inputStream.read()
            if (data == -1) {
                isReading = false
            }
            if (data == 0xFF) {
                val current = inputStream.read().toByte()
                if (current == DQT) {
                    val dqtTables = DqtSegment.decode(inputStream)
                    numOfTableRead += dqtTables.size
                    jpgHeader.dqtSegment.add(dqtTables)
                    if (numOfTableRead >= 3) {
                        isReading = false
                    }
                } else if (current == SOS) {
                    isReading = false
                }
            }
        }
    }

    private fun encryptMessage(key: String, plainText: String): String {
        val transpositionCipher = MyszkowskiCipher()
        val substitutionCipher = AutokeyCipher()

        return transpositionCipher.encrypt(plainText, key).let { stepOne ->
            substitutionCipher.encrypt(stepOne, key).let { stepTwo ->
                transpositionCipher.encrypt(stepTwo, key)
            }
        }
    }

    private fun decryptMessage(cipherText: String, key: String): String {
        val transpositionCipher = MyszkowskiCipher()
        val substitutionCipher = AutokeyCipher()

        return transpositionCipher.decrypt(cipherText, key).let { stepOne ->
            substitutionCipher.decrypt(stepOne, key).let { stepTwo ->
                transpositionCipher.decrypt(stepTwo, key)
            }
        }
    }

    private fun embedMessage(header: JpgHeader, cipherText: String) {
        var currentBit = 0
        var currentChar = 0
        for (component in 3 - 1 downTo 0) {
            for (y in 0 until header.mcuHeight) {
                for (x in 0 until header.mcuWidth) {
                    val currBlock = header.blocks[y * header.mcuWidth + x]
                    for (i in 63 downTo 4) {
                        val coeff = currBlock.getC(component)[i]
                        if (i in listOf(8, 9, 10, 16, 17, 24)) {
                            continue
                        }
                        if (coeff > 1 || coeff < -1) {
                            val bitMessage = cipherText[currentChar].getBit(currentBit)
                            currBlock.getC(component)[i] = coeff.setLsb(bitMessage)
                            currentBit = (currentBit + 1) % 8
                            if (currentBit == 0) {
                                currentChar++
                            }
                            if (currentChar >= cipherText.length) return
                        }
                    }
                }
            }
        }
    }

    private fun extractMessage(header: JpgHeader, messageLength: Int): ByteArray {
        val textBytes: MutableList<Byte> = mutableListOf()
        var currentBit = 0
        var currentChar = 0
        for (component in 3 - 1 downTo 0) {
            for (y in 0 until header.mcuHeight) {
                for (x in 0 until header.mcuWidth) {
                    val currBlock = header.blocks[y * header.mcuWidth + x]
                    for (i in 63 downTo 4) {
                        val coeff = currBlock.getC(component)[i]
                        if (i in listOf(8, 9, 10, 16, 17, 24)) {
                            continue
                        }
                        if (coeff > 1 || coeff < -1) {
                            val bitMessage = coeff.getLsb()
                            currentChar = (currentChar or (bitMessage shl currentBit))
                            currentBit = (currentBit + 1)
                            if (currentBit == 8) {
                                textBytes.add(currentChar.toByte())
                                currentChar = 0
                                currentBit = 0
                            }
                            if (textBytes.size >= messageLength) return textBytes.toByteArray()
                        }
                    }
                }
            }
        }
        return textBytes.toByteArray()
    }

    override suspend fun encodeImage(
        uri: Uri,
        key: String,
        message: String,
        isSampled: Boolean
    ): Uri {
        uri.path?.let {
            val contentResolver = context.contentResolver
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            if (inputStream != null) {
                try {
                    val fileExtension = contentResolver.getType(uri)
                    val isJpg = fileExtension == "image/jpeg"
                    val imageBitmap = getBitmap(contentResolver, uri, isSampled)
                    val outputUri = getImageUri(context)
                    val header = JpgHeader.build(imageBitmap)
                    if (isJpg) {
                        readDQT(inputStream, header)
                    }
                    JpgEncoder.processBlocks(imageBitmap, header)

                    if (message.isNotEmpty()) {
                        val cipherText = encryptMessage(key, message)
                        embedMessage(header, cipherText)
                    }

                    inputStream.close()

                    JpgEncoder.computeDcPrevious(header.blocks)
                    ZigzagScanner.zigzagImageBlocks(header.blocks)

                    val rleDcLuminance: MutableList<Int> = mutableListOf()
                    val rleAcLuminance: MutableList<Int> = mutableListOf()
                    val rleDcChrominance: MutableList<Int> = mutableListOf()
                    val rleAcChrominance: MutableList<Int> = mutableListOf()
                    for (mcuIndex in 0 until header.mcuWidthReal * header.mcuHeightReal) {
                        for (cIndex in 0 until header.sosSegment.numComponent) {
                            val currBlock = header.blocks[mcuIndex]
                            if (currBlock.isCOneOnly && cIndex > 0) continue
                            val rleResult = JpgEncoder.computeRLE(currBlock.getC(cIndex))
                            (if (cIndex == 0) rleDcLuminance else rleDcChrominance).add(rleResult.first())
                            (if (cIndex == 0) rleAcLuminance else rleAcChrominance).addAll(
                                rleResult.drop(
                                    1
                                )
                            )
                        }
                    }

                    val skipHuffmanBuild = (
                            header.sofSegment.height > 2000 || header.sofSegment.width > 2000
                            )

                    val acLumiHuffmanTable = JpgEncoder.buildHuffmanTree(
                        rleAcLuminance,
                        isAcTable = true,
                        isLuma = true,
                        skipBuild = skipHuffmanBuild
                    )
                    val dcLumiHuffmanTable = JpgEncoder.buildHuffmanTree(
                        rleDcLuminance,
                        isAcTable = false,
                        isLuma = true,
                        skipBuild = skipHuffmanBuild
                    )
                    val acChromHuffmanTable = JpgEncoder.buildHuffmanTree(
                        rleAcChrominance,
                        isAcTable = true,
                        isLuma = false,
                        skipBuild = skipHuffmanBuild
                    )
                    val dcChromHuffmanTable = JpgEncoder.buildHuffmanTree(
                        rleDcChrominance,
                        isAcTable = false,
                        isLuma = false,
                        skipBuild = skipHuffmanBuild
                    )

                    withContext(Dispatchers.IO) {
                        val outStream =
                            BufferedOutputStream(context.contentResolver.openOutputStream(outputUri))

                        val jpgEncoder = JpgEncoder(outStream)
                        jpgEncoder.apply {
                            writeHeaders()
                            for (dqt in header.dqtSegment.dqtTables) {
                                if (dqt != null) {
                                    writeDQT(dqt.data, dqt.id)
                                }
                            }
                            writeComment(message.length.toString())
                            writeSOF0(
                                header.sofSegment.width,
                                header.sofSegment.height,
                                header.sofSegment.components
                            )
                            writeDHT(acLumiHuffmanTable, true, 0)
                            writeDHT(dcLumiHuffmanTable, false, 0)
                            writeDHT(acChromHuffmanTable, true, 1)
                            writeDHT(dcChromHuffmanTable, false, 1)
                            writeSOS(header.sosSegment)

                            val huffmanBitstream: MutableList<Byte> = mutableListOf()
                            val bitWriter = BitWriter(huffmanBitstream)
                            for (y in 0 until header.mcuHeight) {
                                for (x in 0 until header.mcuWidth) {
                                    for (cIndex in 0 until 3) {
                                        encodeBlockComponent(
                                            bitWriter,
                                            header.blocks[y * header.mcuWidth + x].getC(cIndex),
                                            if (cIndex == 0) dcLumiHuffmanTable else dcChromHuffmanTable,
                                            if (cIndex == 0) acLumiHuffmanTable else acChromHuffmanTable
                                        )
                                    }

                                }
                            }
                            jpgEncoder.writeHuffmanBitstream(huffmanBitstream)

                            writeFooter()
                        }
                        outStream.close()
                    }

                    return outputUri
                } catch (npe: NullPointerException) {
                    throw NullPointerException("Output Invalid = ${npe.localizedMessage}")
                } catch (io: IOException) {
                    throw IOException("Error Selama Membaca/Menulis File = ${io.localizedMessage}")
                }
            } else {
                throw FileNotFoundException("Tidak Dapat Membaca Gambar")
            }

        }
        throw FileNotFoundException("Gambar Tidak Ditemukan Untuk Di-Encode")
    }

    override suspend fun decodeImage(uri: Uri, key: String): String {
        uri.path?.let {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            if (inputStream != null) {
                return decodeImage(inputStream, key)
            } else {
                throw FileNotFoundException("Uri Not Found")
            }

        }
        throw FileNotFoundException("Uri Not Found")
    }

    override suspend fun compareImage(
        originalImagePath: Uri,
        modifiedImagePath: Uri
    ): ImageMetricsResult =
        withContext(Dispatchers.IO) {
            getBitmap(context.contentResolver, originalImagePath).let { oriBitmap ->
                getBitmap(context.contentResolver, modifiedImagePath).let { modBitmap ->
                    val (mseOne, mseTwo, mseThree) = ImageMetrics.meanSquareError(
                        oriBitmap,
                        modBitmap,
                        false
                    )
                    val combinedMse = (mseOne + mseTwo + mseThree) / 3
                    val psnr = ImageMetrics.peakSignalToNoiseRatio(combinedMse, false)
//                    val ssim = ImageMetrics.structuralSimilarityGrayscale(
//                        oriBitmap,
//                        modBitmap
//                    )

                    val result = ImageMetricsResult(
                        mseOne, mseTwo, mseThree, combinedMse, psnr
                    )
                    return@withContext result
                }
            }
        }


    override suspend fun decodeImage(inputStream: InputStream, key: String): String =
        withContext(Dispatchers.IO) {
            try {
                val jpegDecoder = JpgDecoder(inputStream)
                val comment = jpegDecoder.decodeHeader()

                val jpegHeader = jpegDecoder.jpgHeader

                if (comment.isBlank() || comment.all { !it.isDigit() }) {
                    throw Exception("Pesan Rahasia Tidak Dapat Ditemukan Pada Gambar")
                }


                if (!jpegHeader.sofSegment.isSet) {
                    throw Exception("SOF Marker Tidak Terdeteksi")
                }

                if (!jpegHeader.sosSegment.isSet) {
                    throw Exception("SOS Marker Tidak Terdeteksi")
                }

                jpegHeader.apply {
                    val huffmanDecoder = HuffmanDecoder(
                        inputStream,
                        dhtSegment.dcTables,
                        dhtSegment.acTables
                    )

                    var dcPrev = IntArray(3) { 0 }

                    for (y in 0 until mcuHeight step vSF) {
                        for (x in 0 until mcuWidth step hSF) {
                            if (restartInterval != -1 && (y * mcuWidthReal + x) % restartInterval == 0) {
                                dcPrev = IntArray(3) { 0 }
                                huffmanDecoder.align()
                            }

                            for (i in 0 until sofSegment.componentCount) {
                                val component = sosSegment.components[i]
                                for (v in 0 until vSF) {
                                    for (h in 0 until hSF) {
                                        val currBlock =
                                            jpegHeader.blocks[(y + v) * mcuWidthReal + (x + h)].also {
                                                it.isCOneOnly = v >= 1 || h >= 1
                                            }
                                        if (currBlock.isCOneOnly && i > 0) continue
                                        huffmanDecoder.decode(
                                            currBlock,
                                            dcPrev,
                                            component
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                val cipherBytes = extractMessage(jpegHeader, comment.toInt())
                val cipherText = String(cipherBytes, Charsets.ISO_8859_1)
                return@withContext decryptMessage(cipherText, key)

            } catch (npe: NullPointerException) {
                throw NullPointerException("Tempat Masukan Tidak Valid")
            } catch (fnfe: FileNotFoundException) {
                throw FileNotFoundException("File Tidak Ditemukan Pada Tempat Masukan")
            } catch (iae: IllegalArgumentException) {
                throw IllegalArgumentException("Gambar Masukan Bukan File JPEG")
            } finally {
                inputStream.close()
            }
        }


    override suspend fun copyImageToDestination(
        originalPath: Uri,
        destinationPath: Uri
    ): Unit = withContext(Dispatchers.IO) {
        val contentResolver = context.contentResolver
        contentResolver.openInputStream(originalPath)?.let { inputStream ->
            contentResolver.openOutputStream(destinationPath)?.let { outputStream ->
                val buffer = ByteArray(4 * 1024) // buffer size
                while (true) {
                    val byteCount = inputStream.read(buffer)
                    if (byteCount < 0) break
                    outputStream.write(buffer, 0, byteCount)
                }
                outputStream.close()
                inputStream.close()
            }
        }
    }

    private suspend fun copyImageToDestination(
        image: Bitmap,
        destinationPath: Uri
    ): Unit = withContext(Dispatchers.IO) {
        val contentResolver = context.contentResolver
        contentResolver.openOutputStream(destinationPath)?.let { outputStream ->
            image.compress(Bitmap.CompressFormat.PNG, 0, outputStream)
            outputStream.close()
        }
    }
}
