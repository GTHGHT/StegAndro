package io.github.gthght.stegandro.data.locale.jpg.data.structure

import android.graphics.Bitmap
import io.github.gthght.stegandro.data.locale.jpg.data.segment.DhtSegment
import io.github.gthght.stegandro.data.locale.jpg.data.segment.DqtSegment
import io.github.gthght.stegandro.data.locale.jpg.data.segment.SofSegment
import io.github.gthght.stegandro.data.locale.jpg.data.segment.SosSegment
import io.github.gthght.stegandro.data.locale.jpg.data.segment.subsegment.DqtTable
import io.github.gthght.stegandro.data.locale.jpg.data.segment.subsegment.SofComponent
import io.github.gthght.stegandro.data.locale.jpg.data.segment.subsegment.SosComponent
import io.github.gthght.stegandro.data.locale.util.chrominanceLowQT
import io.github.gthght.stegandro.data.locale.util.chrominanceQT
import io.github.gthght.stegandro.data.locale.util.luminanceLowQT
import io.github.gthght.stegandro.data.locale.util.luminanceQT


class JpgHeader(
    var dqtSegment: DqtSegment = DqtSegment(),
    var dhtSegment: DhtSegment = DhtSegment(),
    var sofSegment: SofSegment = SofSegment(),
    var sosSegment: SosSegment = SosSegment(),
    var restartInterval: Int = -1,

    var mcuHeight: Int = 0,
    var mcuWidth: Int = 0,
    var mcuHeightReal: Int = 0,
    var mcuWidthReal: Int = 0,

    var hSF: Int = 1,
    var vSF: Int = 1,

    var blocks: MutableList<ImageBlock> = mutableListOf()
) {
    companion object {
        fun build(image: Bitmap): JpgHeader {
            val sofComponents = listOf(
                SofComponent(1, 1, 1, 0),
                SofComponent(2, 1, 1, 1),
                SofComponent(3, 1, 1, 1)
            )

            val sofSegment =
                SofSegment(8, image.width, image.height, sofComponents, zeroBased = false, isSet = true)

            val sosComponent = listOf(
                SosComponent(1, 0, 0),
                SosComponent(2, 1, 1),
                SosComponent(3, 1, 1),
            )

            val sosSegment = SosSegment(sosComponent, 3, isSet = true)


            val dqtSegment = DqtSegment().apply {
                if (image.width > 1000 || image.height > 1000) {
                    add(DqtTable(0, luminanceQT, 0))
                    add(DqtTable(1, chrominanceQT, 0))
                } else {
                    add(DqtTable(0, luminanceLowQT, 0))
                    add(DqtTable(1, chrominanceLowQT, 0))
                }

            }
            val mcuHeight = (image.height + 7) / 8
            val mcuWidth = (image.width + 7) / 8

            val header = JpgHeader(
                dqtSegment = dqtSegment,
                sofSegment = sofSegment,
                sosSegment = sosSegment,
                mcuHeight = mcuHeight,
                mcuHeightReal = mcuHeight,
                mcuWidth = mcuWidth,
                mcuWidthReal = mcuWidth
            )
            return header
        }
    }
}