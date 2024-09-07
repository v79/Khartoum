package org.liamjd.pi

import kotlinx.cinterop.ExperimentalForeignApi
import libbcm.bcm2835_init
import org.liamjd.pi.console.printErr
import org.liamjd.pi.ePaper.EPDModel
import org.liamjd.pi.ePaper.EPaperDisplay
import org.liamjd.pi.khartoum.KhartoumImage
import org.liamjd.pi.khartoum.Rotation
import platform.posix.exit

/**
 * Main entry point for the Raspberry Pi application.
 * This initializes the eInk display, and sets up the button actions.
 * It then enters a loop, waiting for button presses to change the display mode.
 */
@OptIn(ExperimentalForeignApi::class)
fun main() {
    if (bcm2835_init() != 1) {
        printErr("Error initializing bcm2838. Exiting")
        exit(-1)
    } else {
        println("bcm2835_init() succeeded and ready to initialize ePaper")
    }

    var mode: DisplayMode = Clock()

    // Initialize the ePaper display
    println("Initializing ePaper display")
    val ePaper = EPaperDisplay(EPDModel.TWO_IN7_B).also {
        it.clear()
        it.delay(2000u)

        it.buttonActions[5u] = {
           mode = Spotify()
        }
        it.buttonActions[6u] = {
            mode = Clock()
        }
        it.buttonActions[13u] = {
            mode = Weather()
        }
        it.buttonActions[19u] = {
            mode = Shutdown()
        }
    }

    ePaper.readBusy()

    // Set up the paintable images
    val blackImage = KhartoumImage(ePaperModel = ePaper.model)
    val redImage = KhartoumImage(ePaperModel = ePaper.model)
    blackImage.reset(Rotation.CW)
    redImage.reset(Rotation.CW)

    // Enter the main loop
    println("Polling for button presses")
    do {
        val buttonPressed = ePaper.pollKeys()
        if (buttonPressed != null) {
           println("Switching to mode $mode")
        }
    } while (mode !is Shutdown)

    // Shut down the ePaper display
    // shut down ePaper
    println("Shutting down ePaper display and exiting")
    ePaper.sleep()
    ePaper.exit()

    exit(0)

}