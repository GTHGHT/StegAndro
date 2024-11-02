package io.github.gthght.stegandro.data.locale.crypto

class AutokeyCipher {
    /**
     * Encrypts the provided plain text using the Autokey Cipher algorithm with ANSI (Windows-1252) alphabet.
     */
    fun encrypt(plainText: String, key: String): String {
        val padLength = plainText.length - key.length
        val keyStream = key + plainText.substring(0, padLength)
        val pTextBytes = plainText.toByteArray(Charsets.ISO_8859_1)
        val keyStreamBytes = keyStream.toByteArray(Charsets.ISO_8859_1)
        val cTextBytes = pTextBytes.mapIndexed { pos, current ->
            ((((current) + (keyStreamBytes[pos])) % 255)).toByte()
        }.toByteArray()

        val result = cTextBytes.toString(Charsets.ISO_8859_1)

        return result
    }

    fun decrypt(cipherText: String, key: String): String {
        val cTextBytes = cipherText.toByteArray(Charsets.ISO_8859_1)
        val keyStreamBytes = key.toByteArray(Charsets.ISO_8859_1).copyOf(cipherText.length)

        val pTextBytes = cTextBytes.mapIndexed { index, current ->
            ((((current) - (keyStreamBytes[index])) % 255)).toByte().also {
                if (index + key.length < cipherText.length) {
                    keyStreamBytes[index + key.length] = it
                }
            }
        }.toByteArray()

        val result = pTextBytes.toString(Charsets.ISO_8859_1)

        return result
    }

}