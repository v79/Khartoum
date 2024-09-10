package org.liamjd.pi

import kotlinx.cinterop.ExperimentalForeignApi
import libbcm.bcm2835_init
import org.liamjd.pi.console.printErr
import org.liamjd.pi.datasources.Clock
import org.liamjd.pi.ePaper.EPDModel
import org.liamjd.pi.ePaper.EPaperDisplay
import platform.posix.exit
import platform.posix.sleep

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

    val clockMode = Clock()
    val spotify = Spotify()
    val weather = Weather()
    val shutdown = Shutdown()

    var mode: DisplayMode = clockMode

    // Initialize the ePaper display
    println("Initializing ePaper display")
    val ePaper = EPaperDisplay(EPDModel.TWO_IN7_B).also {
        it.clear()
        it.delay(2000u)

        it.buttonActions[5u] = {
            mode = spotify
        }
        it.buttonActions[6u] = {
            mode = clockMode
        }
        it.buttonActions[13u] = {
            mode = weather
        }
        it.buttonActions[19u] = {
            mode = shutdown
        }
        // must call refresh to set the initial image
        mode.refresh(it.model)
    }

    ePaper.readBusy()

    // display the initial image
    println("Displaying initial image for mode $mode")
    ePaper.display(mode.images)

    // Enter the main loop
    println("Polling for button presses")
    var seconds = 0u
    do {
        sleep(1u) // sleep for second
        seconds++
        val buttonPressed = ePaper.pollKeys()
        if (buttonPressed != null) {
            println("Switching to mode $mode")
        }
        if (seconds >= 120u || buttonPressed != null) {
            println("Refreshing display for mode $mode")
            mode.refresh(ePaper.model)
            ePaper.display(mode.images)
            seconds = 0u
        }
    } while (mode !is Shutdown)

    // Shut down the ePaper display
    // shut down ePaper
    println("Shutting down ePaper display and exiting")
    ePaper.clear()
    ePaper.sleep()
    ePaper.exit()

    exit(0)

}