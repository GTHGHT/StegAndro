package io.github.gthght.stegandro.data.locale.jpg.data.huffman

import java.util.PriorityQueue

/**
 * This class represents a node in a Huffman tree specific for building a huffman tree .
 *
 * A Huffman tree is a binary tree where each leaf node represents a symbol, and its
 * frequency is stored in the node. The weight of each internal node is the sum of the
 * frequencies of its children. Symbols with higher frequencies are assigned shorter codes
 * for efficient encoding.
 *
 * This class inherits from the `Comparable<HuffmanNode>` interface to enable comparison
 * based on node frequencies.
 */
class HuffmanNodeWrite : Comparable<HuffmanNodeWrite>, HuffmanNode() {
    var frequency: Int = 0

    override fun compareTo(other: HuffmanNodeWrite): Int = this.frequency - other.frequency

    override fun toString(): String {
        return "NodeWrite($symbol, $frequency, $isLeaf)"
    }

    companion object {
        /**
         * This function takes a list of `Pair<Int, Int>` representing symbol-frequency pairs
         * and builds a Huffman tree using these frequencies.
         *
         * @param frequencyPairs A list of pairs where the first element is the symbol (an integer)
         * and the second element is its frequency (an integer).
         * @return The root node of the constructed Huffman tree, or null if the input list is empty.
         */
        fun encode(frequencyPairs: List<Pair<Int, Int>>): HuffmanNodeWrite? {
            // makes a min-priority queue(min-heap)
            if (frequencyPairs.size == 1) {
                val parentNode = HuffmanNodeWrite()
                val childNode = HuffmanNodeWrite().apply {
                    val (symbol, count) = frequencyPairs.first()
                    isLeaf = true
                    this.symbol = symbol
                    frequency = count
                }
                parentNode.node0 = childNode
                childNode.parent = parentNode
                return parentNode
            }

            if (frequencyPairs.isEmpty()) return null
            val queue = PriorityQueue(frequencyPairs.size, HuffmanNodeWrite::compareTo)

            for ((symbol, count) in frequencyPairs) {
                val huffNode = HuffmanNodeWrite().apply {
                    isLeaf = true
                    this.symbol = symbol
                    frequency = count
                }

                queue.add(huffNode)
            }
            /** In the JPEG Algorithm, there can't be a node that have entirely 1 with their codes.
             * So, when creating huffman tree, you must prevent a leaf located at the end of right node.
             * You can do this by adding a branch node with the frequency half of the lowest leaf node frequency.
             *
             * **See Also:** [Huffman Tree Example With The Forbidden All 1](https://drive.google.com/uc?export=view&id=1bH8-KHPv2RwHctSlHG_IuyDMfzVvjD2p)
             */
            val minFrequencyPair = frequencyPairs.minBy { it.second }
            val virtualNode = HuffmanNodeWrite().apply {
                isLeaf = false
                frequency = minFrequencyPair.second
            }
            queue.add(virtualNode)

            var rootNode: HuffmanNodeWrite? = null

            // Here we will extract the two minimum value
            // from the heap each time until
            // its size reduces to 1, extract until
            // all the nodes are extracted.
            while (queue.size > 1) {
                val first = queue.poll()
                val second = queue.poll()
                val parentNode = HuffmanNodeWrite()

                if (first== null || second == null){
                    throw Exception("Leaf Huffman Encoding Unexpectedly Null")
                }

                parentNode.frequency = first.frequency + second.frequency

                if (queue.size == 0) {
                    parentNode.node0 = first
                    parentNode.node1 = second
                } else if (first.isLeaf) {
                    parentNode.node0 = first
                    parentNode.node1 = second
                } else if (second.isLeaf) {
                    parentNode.node0 = second
                    parentNode.node1 = first
                } else {
                    parentNode.node0 = first
                    parentNode.node1 = second
                }

                first.parent = parentNode
                second.parent = parentNode

                rootNode = parentNode
                queue.add(parentNode)
            }

            return rootNode
        }
    }

}