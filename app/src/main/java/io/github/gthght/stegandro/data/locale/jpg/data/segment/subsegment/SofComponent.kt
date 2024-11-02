package io.github.gthght.stegandro.data.locale.jpg.data.segment.subsegment

/**
 * Represents a component in the Start of Frame (SOF) section of a JPEG image.
 *
 * This class encapsulates information about a specific image component, including its unique identifier,
 * horizontal and vertical sampling factors, and the associated quantum table identifier.
 *
 * @property id The unique identifier of the image component.
 * @property hSF The horizontal sampling factor used for image compression.
 * @property vSF The vertical sampling factor used for image compression.
 * @property qtId The identifier of the quantum table associated with the component.
 */
class SofComponent(val id: Int, val hSF: Int, val vSF: Int, val qtId: Int) {

    override fun toString(): String {
        return "SOFComponent(id=$id, hSF=$hSF, vSF=$vSF, qtId=$qtId)"
    }
}