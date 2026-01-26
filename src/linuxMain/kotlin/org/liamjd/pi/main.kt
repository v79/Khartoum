package org.liamjd.pi

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.staticCFunction
import libbcm.bcm2835_init
import org.liamjd.pi.console.printErr
import org.liamjd.pi.datasources.Clock
import org.liamjd.pi.datasources.Spotify
import org.liamjd.pi.ePaper.EPDModel
import org.liamjd.pi.ePaper.EPaperDisplay
import platform.posix.*

// The SIGINT and SIGTERM signals are used to gracefully shut down the application
// They can only access global variables, so we need to declare the mode variable as global.
// Defaulting to an empty display mode
var mode: DisplayMode = Blank()

/**
 * Main entry point for the Raspberry Pi application.
 * This initialises the eInk display and sets up the button actions.
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

    // set up SIGTERM and SIGINT handlers
    signal(SIGINT, staticCFunction<Int, Unit> {
        shutdownDisplay()
    })
    signal(SIGTERM, staticCFunction<Int, Unit> {
        shutdownDisplay()
    })

    mode = spotify

    // Initialize the ePaper display
    println("Initializing ePaper display")
    val ePaper = EPaperDisplay(EPDModel.TWO_IN7_B).also {
        it.clear()
        it.delay(2000u)

        // Key 1
        it.buttonActions[5u] = {
            mode = spotify
        }
        // Key 2
        it.buttonActions[6u] = {
            mode = clockMode
        }
        // Key 3, not implemented
        it.buttonActions[13u] = {
            mode = weather
        }
        // Key 4
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
        sleep(1u) // sleep for one second
    } while (mode !is Shutdown)

    ePaper.shutdown()

    exit(0)

}

/**
 * Function to handle the shutdown of the application
 */
fun shutdownDisplay() {
    println("SIGINT or SIGTERM received, shutting down")
    mode = Shutdown()
}