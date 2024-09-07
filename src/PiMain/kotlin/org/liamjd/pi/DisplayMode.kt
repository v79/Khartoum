package org.liamjd.pi

import platform.posix.uint8_t

sealed class DisplayMode(buttonId: uint8_t) {
    class Clock : DisplayMode(5u)
    class Spotify : DisplayMode(6u)
    class Weather : DisplayMode(13u)
    class Shutdown : DisplayMode(19u)
}