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

/**
 * A blank display mode which does nothing and displays nothing
 */
class Blank(override val id: uint8_t = 0u) : DisplayMode {
    private var black: KhartoumImage = KhartoumImage(0, 0)
    private var red: KhartoumImage = KhartoumImage(0, 0)

    override fun toString(): String {
        return "Blank"
    }

    override val images
        get() = arrayOf(black.bytes, red.bytes)

    override fun refresh(ePaperModel: EPDModel) {
        black = KhartoumImage(ePaperModel.pixelWidth, ePaperModel.pixelHeight)
        red = KhartoumImage(ePaperModel.pixelWidth, ePaperModel.pixelHeight)
        black.reset(Rotation.CW)
        red.reset(Rotation.CW)
    }
}

class Weather(override val id: uint8_t = 13u) : DisplayMode {
    private var black: KhartoumImage = KhartoumImage(0, 0)
    private var red: KhartoumImage = KhartoumImage(0, 0)

    override fun toString(): String {
        return "Clock"
    }

    override val images
        get() = arrayOf(black.bytes, red.bytes)


    override fun refresh(ePaperModel: EPDModel) {
        println("Weather not implemented yet :)")

        black = KhartoumImage(ePaperModel.pixelWidth, ePaperModel.pixelHeight)
        red = KhartoumImage(ePaperModel.pixelWidth, ePaperModel.pixelHeight)
        black.reset(Rotation.CW)
        red.reset(Rotation.CW)
        black.drawString(1, 1, "Weather", KhFont.CascadiaCodeSemiBold24, false, wrapMode = TextWrapMode.WRAP)
    }
}

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
        black = KhartoumImage(ePaperModel.pixelWidth, ePaperModel.pixelHeight)
        red = KhartoumImage(ePaperModel.pixelWidth, ePaperModel.pixelHeight)
        black.reset(Rotation.CW)
        red.reset(Rotation.CW)
        val message = "Shutdown"

        red.drawString(
            0,
            0,
            message,
            font = KhFont.CascadiaCodeSemiBold24,
            wrapMode = TextWrapMode.WRAP
        )
    }
}
