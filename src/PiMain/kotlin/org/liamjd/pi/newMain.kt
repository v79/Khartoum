package org.liamjd.pi

import kotlinx.cinterop.ExperimentalForeignApi
import libbcm.bcm2835_init
import org.liamjd.pi.console.printErr
import org.liamjd.pi.ePaper.EPDModel
import org.liamjd.pi.ePaper.EPaperDisplay
import platform.posix.exit

/**
 * Main entry point for the Raspberry Pi application.
 * This initializes the eInk display, and sets up the button actions.
 * It then enters a loop, waiting for button presses to change the display mode.
 */
@OptIn(ExperimentalForeignApi::class)
fun newmain() {
    if (bcm2835_init() != 1) {
        printErr("Error initializing bcm2838. Exiting")
        exit(-1)
    } else {
        println("bcm2835_init() succeeded")
    }

    var mode: DisplayMode = DisplayMode.Clock()

    // Initialize the ePaper display
    val ePaper = EPaperDisplay(EPDModel.TWO_IN7_B).also {
        it.clear()
        it.delay(2000u)

    /*    it.buttonActions[5u] = {
           mode = DisplayMode.Spotify()
        }
        it.buttonActions[6u] = {
            mode = DisplayMode.Clock()
        }
        it.buttonActions[13u] = {
            mode = DisplayMode.Weather()
        }
        it.buttonActions[19u] = {
            mode = DisplayMode.Shutdown()
        }*/
    }

    ePaper.readBusy()

    // Enter the main loop


    // Shut down the ePaper display
    // shut down ePaper
    ePaper.sleep()
    ePaper.exit()

    exit(0)

}