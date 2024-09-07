package org.liamjd.pi

import platform.posix.uint8_t

sealed class DisplayMode(buttonId: uint8_t) {

}

class Clock : DisplayMode(5u) {
    override fun toString(): String {
        return "Clock"
    }
}

class Spotify : DisplayMode(6u) {
    override fun toString(): String {
        return "Spotify"
    }
}

class Weather : DisplayMode(13u) {
    override fun toString(): String {
        return "Weather"
    }
}

class Shutdown : DisplayMode(19u) {
    override fun toString(): String {
        return "Shutdown"
    }
}