package io.github.gthght.stegandro.data.locale.jpg.data.huffman

class HuffmanTree {
    var root: HuffmanNodeRead = HuffmanNodeRead()
    var currentBranch: HuffmanNodeRead
    private var branches: MutableList<HuffmanNodeRead>

    init {
        this.currentBranch = root
        branches = ArrayList()
        branches.add(root)
    }

    fun fillLevel() {
        val level = currentBranch.level()
        while (nextBranch().level() == level) {
            addNewBranch()
        }
    }

    private fun addNewBranch(): HuffmanTree {
        val branch = HuffmanNodeRead()
        nextBranch().addChild(branch)
        branch.index = branches.size
        branches.add(branch)
        return this
    }

    fun addLeaf(code: Int): HuffmanTree {
        val leaf = HuffmanNodeRead(code)
        nextBranch().addChild(leaf)
        return this
    }

    private fun nextBranch(): HuffmanNodeRead {
        if (currentBranch.isFull) {
            currentBranch = branches[currentBranch.index + 1]
        }
        return currentBranch
    }
}
