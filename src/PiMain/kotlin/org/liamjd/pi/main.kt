package org.liamjd.pi

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import kotlinx.datetime.Clock
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import libbcm.bcm2835_init
import libcurl.*
import org.liamjd.pi.ePaper.EPDModel
import org.liamjd.pi.ePaper.EPaperDisplay
import org.liamjd.pi.khartoum.KhFont
import org.liamjd.pi.khartoum.KhartoumImage
import org.liamjd.pi.khartoum.Rotation
import org.liamjd.pi.khartoum.TextWrapMode
import platform.posix.exit

@OptIn(ExperimentalForeignApi::class)
fun main() {
    println("Hello, from Kotlin Native!")

    val now = Clock.System.now()
    val dateFormat = DateTimeComponents.Format {
        dayOfMonth(padding = Padding.SPACE)
        char('/')
        monthNumber(padding = Padding.ZERO)
        char('/')
        year()
        char(' ')
        hour(padding = Padding.SPACE)
        char(':')
        minute(padding = Padding.ZERO)
    }

    if (bcm2835_init() != 1) {
        println("Error initializing bcm2838. Exiting")
        exit(-1)
    } else {
        println("bcm2835_init() succeeded")
    }

    val ePaper = EPaperDisplay(EPDModel.TWO_IN7_B).also {
        it.clear()
        it.delay(2000u)
    }

    ePaper.readBusy()

    val blackImage = KhartoumImage(ePaperModel = ePaper.model)
    val redImage = KhartoumImage(ePaperModel = ePaper.model)

    blackImage.reset(Rotation.CW)
    redImage.reset(Rotation.CW)

    println("Painting black image")
    blackImage.drawString(
        xStart = 0,
        yStart = 0,
        string = "Hello from Kotlin Native",
        font = KhFont.CascadiaMono12,
        wrapMode = TextWrapMode.WRAP
    )

    println("Painting red image")
    redImage.drawString(
        xStart = 0,
        yStart = 48,
        string = now.format(dateFormat),
        font = KhFont.CascadiaMono12,
        wrapMode = TextWrapMode.WRAP
    )

    println("Displaying black and red images")
    ePaper.display(arrayOf(blackImage.bytes, redImage.bytes))

    // shut down ePaper
    ePaper.sleep()
    ePaper.exit()

    exit(0)

}

class Curl {

    @OptIn(ExperimentalForeignApi::class)
    fun call_curl() {
        val curl = curl_easy_init()
        if (curl != null) {
            curl_easy_setopt(curl, CURLOPT_URL, "https://www.liamjd.org")
            val res = curl_easy_perform(curl)
            if (res != CURLE_OK) {
                println("curl_easy_perform() failed ${curl_easy_strerror(res)?.toKString()}")
            } else {
                println("curl_easy_perform() succeeded")
            }
            curl_easy_cleanup(curl)
        }
    }
}