package org.liamjd.pi

import kotlinx.cinterop.ExperimentalForeignApi
import org.liamjd.pi.ePaper.EPDModel
import org.liamjd.pi.khartoum.KhFont
import org.liamjd.pi.khartoum.KhartoumImage
import org.liamjd.pi.khartoum.Rotation
import org.liamjd.pi.khartoum.TextWrapMode
import platform.posix.uint8_t
import kotlin.experimental.ExperimentalNativeApi

/**
 * Interface for the display modes that the ePaper display can show
 * @param id the unique identifier for the display mode, which corresponds to the button press
 */
interface DisplayMode {
    val id: uint8_t
    override fun toString(): String
    val images: Array<UByteArray>

    /**
     * Update the display by repainting the images
     */
    fun refresh(ePaperModel: EPDModel)
}


class Spotify(override val id: uint8_t = 6u) : DisplayMode {
    override fun toString(): String {
        return "Spotify"
    }

    override val images: Array<UByteArray> = arrayOf()

    override fun refresh(ePaperModel: EPDModel) {
        TODO("Not yet implemented")
    }
}


class Weather(override val id: uint8_t = 13u) : DisplayMode {
    override fun toString(): String {
        return "Weather"
    }

    override val images: Array<UByteArray> = arrayOf()
    override fun refresh(ePaperModel: EPDModel) {
        TODO("Not yet implemented")
    }
}

@OptIn(ExperimentalForeignApi::class)
class Shutdown(override val id: uint8_t = 19u) : DisplayMode {

    private var black: KhartoumImage = KhartoumImage(0, 0)
    private var red: KhartoumImage = KhartoumImage(0, 0)

    override fun toString(): String {
        return "Shutdown"
    }

    override val images
        get() = arrayOf(black.bytes, red.bytes)

    @OptIn(ExperimentalNativeApi::class)
    override fun refresh(ePaperModel: EPDModel) {
        red = KhartoumImage(ePaperModel.pixelWidth, ePaperModel.pixelHeight)
        red.reset(Rotation.CW)
        val message = "Shutdown down"
        val messageSize = red.measureString(message, KhFont.CascadiaCodeSemiBold24, TextWrapMode.WRAP, Rotation.CW)
        println("Message size: $messageSize")
        val startX = (ePaperModel.pixelWidth - messageSize.x) / 2
        val startY = (ePaperModel.pixelHeight - messageSize.y) / 2
        println("Start X: $startX, Start Y: $startY")
        assert(startX >= 0 && startX <= ePaperModel.pixelWidth)
        assert(startY >= 0 && startY <= ePaperModel.pixelHeight)

        red.drawString(
            startX,
            startY,
            "Shutting down",
            font = KhFont.CascadiaCodeSemiBold24,
            wrapMode = TextWrapMode.WRAP
        )
    }
}
