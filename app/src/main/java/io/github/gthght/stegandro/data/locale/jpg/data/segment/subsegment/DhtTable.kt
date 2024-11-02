package io.github.gthght.stegandro.data.locale.jpg.data.segment.subsegment

import io.github.gthght.stegandro.data.locale.jpg.data.huffman.HuffmanTree

class DhtTable(val id: Int, huffmanTree: HuffmanTree, val isAcTable:Boolean) {
    private val tree: HuffmanTree = huffmanTree

    fun getTree(): HuffmanTree {
        return tree
    }
}
