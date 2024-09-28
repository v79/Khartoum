@file:OptIn(ExperimentalForeignApi::class)

package org.liamjd.pi.console

import kotlinx.cinterop.ExperimentalForeignApi

// TODO: Make this a command line argument
const val debug: Boolean = false

/**
 * Print a message to stderr
 */
@OptIn(ExperimentalForeignApi::class)
val STDERR = platform.posix.fdopen(2, "w")

@OptIn(ExperimentalForeignApi::class)
fun printErr(message: String) {
    platform.posix.fprintf(STDERR, "%s\n", message)
    platform.posix.fflush(STDERR)
}

/**
 * Conditionally print a debug message
 */
fun printDebug(message: String) {
    if (debug) {
        println("DEBUG: $message")
    }
}