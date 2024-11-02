package io.github.gthght.stegandro.data.locale.jpg.data.huffman

class HuffmanNodeRead : HuffmanNode {
    var index: Int = 0

    constructor() {
        this.symbol = -1
    }

    constructor(code: Int) {
        this.symbol = code
        isLeaf = true
    }
}