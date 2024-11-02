package io.github.gthght.stegandro.data.locale.jpg.process.subprocess

import io.github.gthght.stegandro.data.locale.jpg.data.structure.ImageBlock
import io.github.gthght.stegandro.data.locale.util.m1
import io.github.gthght.stegandro.data.locale.util.m2
import io.github.gthght.stegandro.data.locale.util.m3
import io.github.gthght.stegandro.data.locale.util.m4
import io.github.gthght.stegandro.data.locale.util.m5
import io.github.gthght.stegandro.data.locale.util.s0
import io.github.gthght.stegandro.data.locale.util.s1
import io.github.gthght.stegandro.data.locale.util.s2
import io.github.gthght.stegandro.data.locale.util.s3
import io.github.gthght.stegandro.data.locale.util.s4
import io.github.gthght.stegandro.data.locale.util.s5
import io.github.gthght.stegandro.data.locale.util.s6
import io.github.gthght.stegandro.data.locale.util.s7
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sqrt

class Dct {

    /**
     * The Length of process.DCT Block for every division
     */
    private val n: Int = 8

    private val c: DoubleArray = DoubleArray(8)

    /**
     * Pre-Calculated Cosines Result Of Cos((2*x)*iπ/2N)
     */
    private val cosines: Array<DoubleArray> = Array(n) {
        DoubleArray(n)
    }

    private val cosinesIDct: Array<DoubleArray> = Array(n) {
        DoubleArray(n)
    }

    init {
        initMatrices()
    }

    private fun initMatrices() {
        for (u in 0 until n) {
            c[u] = if (u == 0) 1.0 / sqrt(2.0) else 1.0
            for (x in 0 until n) {
                cosines[x][u] = cos((2.0 * x + 1.0) * u * PI / (2 * n))
                cosinesIDct[x][u] = cos(((u + .5F) * x * PI) / 8)
            }
        }
    }

    fun dctTransform(imageBlocks: List<ImageBlock>, mcuWidth: Int, mcuHeight: Int) {
        for (i in 0 until mcuWidth * mcuHeight) {
            val currBlock = imageBlocks[i]
            //Convert To Not Optimized If Error
            calcDctOptimized(currBlock.cOne)
            if (currBlock.isCOneOnly) continue
            calcDctOptimized(currBlock.cTwo)
            calcDctOptimized(currBlock.cThree)
        }
    }

    private fun calcDct(block: IntArray) {
        val result = DoubleArray(64)

        for (i in 0 until n) {
            for (v in 0 until n) {
                var sum = 0.0
                for (y in 0 until n) {
                    sum += block[y * 8 + i] * cosines[y][v]
                }
                result[v * 8 + i] = sum * c[v] * 0.5
            }
        }

        for (i in 0 until n) {
            for (u in 0 until n) {
                var sum = 0.0
                for (x in 0 until n) {
                    sum += result[i * 8 + x] * cosines[x][u]
                }
                block[i * 8 + u] = (sum * c[u] * 0.5).toInt()
            }
        }
    }

    private fun calcDctOptimized(block: IntArray) {
        val result = DoubleArray(64)

        for (i in 0 until 8) {
            val a0 = block[0 * 8 + i]
            val a1 = block[1 * 8 + i]
            val a2 = block[2 * 8 + i]
            val a3 = block[3 * 8 + i]
            val a4 = block[4 * 8 + i]
            val a5 = block[5 * 8 + i]
            val a6 = block[6 * 8 + i]
            val a7 = block[7 * 8 + i]

            val b0 = a0 + a7
            val b1 = a1 + a6
            val b2 = a2 + a5
            val b3 = a3 + a4
            val b4 = a3 - a4
            val b5 = a2 - a5
            val b6 = a1 - a6
            val b7 = a0 - a7

            val c0 = b0 + b3
            val c1 = b1 + b2
            val c2 = b1 - b2
            val c3 = b0 - b3
            val c4 = b4
            val c5 = b5 - b4
            val c6 = b6 - c5
            val c7 = b7 - b6

            val d0 = c0 + c1
            val d1 = c0 - c1
            val d2 = c2
            val d3 = c3 - c2
            val d4 = c4
            val d5 = c5
            val d6 = c6
            val d7 = c5 + c7
            val d8 = c4 - c6

            val e0 = d0
            val e1 = d1
            val e2 = d2 * m1
            val e3 = d3
            val e4 = d4 * m2
            val e5 = d5 * m3
            val e6 = d6 * m4
            val e7 = d7
            val e8 = d8 * m5

            val f0 = e0
            val f1 = e1
            val f2 = e2 + e3
            val f3 = e3 - e2
            val f4 = e4 + e8
            val f5 = e5 + e7
            val f6 = e6 + e8
            val f7 = e7 - e5

            val g0 = f0
            val g1 = f1
            val g2 = f2
            val g3 = f3
            val g4 = f4 + f7
            val g5 = f5 + f6
            val g6 = f5 - f6
            val g7 = f7 - f4

            result[0 * 8 + i] = g0 * s0
            result[4 * 8 + i] = g1 * s4
            result[2 * 8 + i] = g2 * s2
            result[6 * 8 + i] = g3 * s6
            result[5 * 8 + i] = g4 * s5
            result[1 * 8 + i] = g5 * s1
            result[7 * 8 + i] = g6 * s7
            result[3 * 8 + i] = g7 * s3
        }
        for (i in 0 until 8) {
            val a0 = result[i * 8 + 0]
            val a1 = result[i * 8 + 1]
            val a2 = result[i * 8 + 2]
            val a3 = result[i * 8 + 3]
            val a4 = result[i * 8 + 4]
            val a5 = result[i * 8 + 5]
            val a6 = result[i * 8 + 6]
            val a7 = result[i * 8 + 7]

            val b0 = a0 + a7
            val b1 = a1 + a6
            val b2 = a2 + a5
            val b3 = a3 + a4
            val b4 = a3 - a4
            val b5 = a2 - a5
            val b6 = a1 - a6
            val b7 = a0 - a7

            val c0 = b0 + b3
            val c1 = b1 + b2
            val c2 = b1 - b2
            val c3 = b0 - b3
            val c4 = b4
            val c5 = b5 - b4
            val c6 = b6 - c5
            val c7 = b7 - b6

            val d0 = c0 + c1
            val d1 = c0 - c1
            val d2 = c2
            val d3 = c3 - c2
            val d4 = c4
            val d5 = c5
            val d6 = c6
            val d7 = c5 + c7
            val d8 = c4 - c6

            val e0 = d0
            val e1 = d1
            val e2 = d2 * m1
            val e3 = d3
            val e4 = d4 * m2
            val e5 = d5 * m3
            val e6 = d6 * m4
            val e7 = d7
            val e8 = d8 * m5

            val f0 = e0
            val f1 = e1
            val f2 = e2 + e3
            val f3 = e3 - e2
            val f4 = e4 + e8
            val f5 = e5 + e7
            val f6 = e6 + e8
            val f7 = e7 - e5

            val g0 = f0
            val g1 = f1
            val g2 = f2
            val g3 = f3
            val g4 = f4 + f7
            val g5 = f5 + f6
            val g6 = f5 - f6
            val g7 = f7 - f4

            block[i * 8 + 0] = (g0 * s0).toInt()
            block[i * 8 + 4] = (g1 * s4).toInt()
            block[i * 8 + 2] = (g2 * s2).toInt()
            block[i * 8 + 6] = (g3 * s6).toInt()
            block[i * 8 + 5] = (g4 * s5).toInt()
            block[i * 8 + 1] = (g5 * s1).toInt()
            block[i * 8 + 7] = (g6 * s7).toInt()
            block[i * 8 + 3] = (g7 * s3).toInt()
        }
    }

    fun iDctTransform(imageBlocks: List<ImageBlock>, mcuWidth: Int, mcuHeight: Int) {
        for (i in 0 until mcuWidth * mcuHeight) {
            val currBlock = imageBlocks[i]
            calcIDctOptimized(currBlock.cOne)
            if (currBlock.isCOneOnly) continue
            calcIDctOptimized(currBlock.cTwo)
            calcIDctOptimized(currBlock.cThree)
        }
    }

    private fun calcIDct(block: IntArray) {
        val result = DoubleArray(64)

        for (i in 0 until n) {
            for (y in 0 until n) {
                var sum = 0.0
                for (v in 0 until n) {
                    sum += block[v * 8 + i] * cosinesIDct[v][y] * c[v] * 0.5
                }
                result[y * 8 + i] = sum
            }
        }

        for (i in 0 until n) {
            for (x in 0 until n) {
                var sum = 0.0
                for (u in 0 until n) {
                    sum += result[i * 8 + u] * cosinesIDct[u][x] * c[u] * 0.5
                }
                block[i * 8 + x] = sum.toInt()
            }
        }
    }

    private fun calcIDctOptimized(block: IntArray) {
        val result = DoubleArray(64)

        for (i in 0 until 8) {
            val g0 = block[0 * 8 + i] * s0
            val g1 = block[4 * 8 + i] * s4
            val g2 = block[2 * 8 + i] * s2
            val g3 = block[6 * 8 + i] * s6
            val g4 = block[5 * 8 + i] * s5
            val g5 = block[1 * 8 + i] * s1
            val g6 = block[7 * 8 + i] * s7
            val g7 = block[3 * 8 + i] * s3

            val f0 = g0
            val f1 = g1
            val f2 = g2
            val f3 = g3
            val f4 = g4 - g7
            val f5 = g5 + g6
            val f6 = g5 - g6
            val f7 = g4 + g7

            val e0 = f0
            val e1 = f1
            val e2 = f2 - f3
            val e3 = f2 + f3
            val e4 = f4
            val e5 = f5 - f7
            val e6 = f6
            val e7 = f5 + f7
            val e8 = f4 + f6

            val d0 = e0
            val d1 = e1
            val d2 = e2 * m1
            val d3 = e3
            val d4 = e4 * m2
            val d5 = e5 * m3
            val d6 = e6 * m4
            val d7 = e7
            val d8 = e8 * m5

            val c0 = d0 + d1
            val c1 = d0 - d1
            val c2 = d2 - d3
            val c3 = d3
            val c4 = d4 + d8
            val c5 = d5 + d7
            val c6 = d6 - d8
            val c7 = d7
            val c8 = c5 - c6

            val b0 = c0 + c3
            val b1 = c1 + c2
            val b2 = c1 - c2
            val b3 = c0 - c3
            val b4 = c4 - c8
            val b5 = c8
            val b6 = c6 - c7
            val b7 = c7

            result[0 * 8 + i] = b0 + b7
            result[1 * 8 + i] = b1 + b6
            result[2 * 8 + i] = b2 + b5
            result[3 * 8 + i] = b3 + b4
            result[4 * 8 + i] = b3 - b4
            result[5 * 8 + i] = b2 - b5
            result[6 * 8 + i] = b1 - b6
            result[7 * 8 + i] = b0 - b7
        }
        for (i in 0 until 8) {
            val g0 = result[i * 8 + 0] * s0
            val g1 = result[i * 8 + 4] * s4
            val g2 = result[i * 8 + 2] * s2
            val g3 = result[i * 8 + 6] * s6
            val g4 = result[i * 8 + 5] * s5
            val g5 = result[i * 8 + 1] * s1
            val g6 = result[i * 8 + 7] * s7
            val g7 = result[i * 8 + 3] * s3

            val f0 = g0
            val f1 = g1
            val f2 = g2
            val f3 = g3
            val f4 = g4 - g7
            val f5 = g5 + g6
            val f6 = g5 - g6
            val f7 = g4 + g7

            val e0 = f0
            val e1 = f1
            val e2 = f2 - f3
            val e3 = f2 + f3
            val e4 = f4
            val e5 = f5 - f7
            val e6 = f6
            val e7 = f5 + f7
            val e8 = f4 + f6

            val d0 = e0
            val d1 = e1
            val d2 = e2 * m1
            val d3 = e3
            val d4 = e4 * m2
            val d5 = e5 * m3
            val d6 = e6 * m4
            val d7 = e7
            val d8 = e8 * m5

            val c0 = d0 + d1
            val c1 = d0 - d1
            val c2 = d2 - d3
            val c3 = d3
            val c4 = d4 + d8
            val c5 = d5 + d7
            val c6 = d6 - d8
            val c7 = d7
            val c8 = c5 - c6

            val b0 = c0 + c3
            val b1 = c1 + c2
            val b2 = c1 - c2
            val b3 = c0 - c3
            val b4 = c4 - c8
            val b5 = c8
            val b6 = c6 - c7
            val b7 = c7

            block[i * 8 + 0] = (b0 + b7 + 0.5f).toInt()
            block[i * 8 + 1] = (b1 + b6 + 0.5f).toInt()
            block[i * 8 + 2] = (b2 + b5 + 0.5f).toInt()
            block[i * 8 + 3] = (b3 + b4 + 0.5f).toInt()
            block[i * 8 + 4] = (b3 - b4 + 0.5f).toInt()
            block[i * 8 + 5] = (b2 - b5 + 0.5f).toInt()
            block[i * 8 + 6] = (b1 - b6 + 0.5f).toInt()
            block[i * 8 + 7] = (b0 - b7 + 0.5f).toInt()
        }
    }
}