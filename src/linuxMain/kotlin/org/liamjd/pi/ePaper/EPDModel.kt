package org.liamjd.pi.ePaper

import platform.posix.uint8_t

/** Enum class representing a few of the Waveshare ePaper display models
 * currently supported.
 * [modelNumber] internal model name of the device
 * [pixelWidth] the width of the display in pixels; it may be a portrait device
 * [pixelHeight] the height of the display in pixels
 * [pins] the set of the primary command GPIO pins used to communicate with the display, see [EPDPins]
 * [buttons] a possible empty set of the GPIO pin numbers associated with physical buttons on the display
 */
@ExperimentalUnsignedTypes
enum class EPDModel(
    val modelNumber: String,
    val pixelWidth: Int,
    val pixelHeight: Int,
    val pins: EPDPins,
    val colours: Set<EPDColours>,
    val buttons: Set<uint8_t>? = emptySet()
) {
    // THIS IS NOT V2!
    TWO_IN7_B(
        modelNumber = "2.7 inch B", pixelWidth = 176, pixelHeight = 264, pins = EPDPins(
            reset = 17u,
            dataHighCommandLow = 25u,
            chipSelect = 8u,
            busy = 24u,
            power = 18u
        ),
        colours = setOf(EPDColours.WHITE, EPDColours.BLACK, EPDColours.RED),
        buttons = setOf(5u,6u, 13u, 19u)
    ),
    TWO_IN7(
        modelNumber = "2.7 inch", pixelWidth = 176, pixelHeight = 264, pins = EPDPins(
            reset = 17u,
            dataHighCommandLow = 25u,
            chipSelect = 8u,
            busy = 24u,
            power = 6u // GUESS!
        ),
        setOf(EPDColours.WHITE, EPDColours.BLACK)
    )

}

/**
 * The set of command/data pins used by the ePaper device
 */
class EPDPins(val reset: uint8_t, val dataHighCommandLow: uint8_t, val chipSelect: uint8_t, val busy: uint8_t, val power: uint8_t)

/**
 * Valid ink colours used by an ePaper device. Note that 'WHITE' is the same as 'no colour'
 */
enum class EPDColours {
    WHITE,
    BLACK,
    RED
}
