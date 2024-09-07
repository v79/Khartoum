package org.liamjd.pi.datasources

import kotlinx.cinterop.ExperimentalForeignApi
import org.liamjd.pi.DisplayMode
import org.liamjd.pi.ePaper.EPDModel
import org.liamjd.pi.khartoum.KhFont
import org.liamjd.pi.khartoum.KhartoumImage
import org.liamjd.pi.khartoum.Rotation
import org.liamjd.pi.khartoum.TextWrapMode
import platform.posix.uint8_t

@OptIn(ExperimentalForeignApi::class)
class Clock(override val id: uint8_t = 6u) : DisplayMode {
    private var black: KhartoumImage = KhartoumImage(0, 0)
    private var red: KhartoumImage = KhartoumImage(0, 0)

    override fun toString(): String {
        return "Clock"
    }

    override val images
        get() = arrayOf(black.bytes, red.bytes)

    override fun refresh(ePaperModel: EPDModel) {
        black = KhartoumImage(ePaperModel.pixelWidth, ePaperModel.pixelHeight)
        red = KhartoumImage(ePaperModel.pixelWidth, ePaperModel.pixelHeight)
        black.reset(Rotation.CW)
        red.reset(Rotation.CW)
        black.drawString(1, 1, "This is a clock", KhFont.CascadiaCodeSemiBold24, false, wrapMode = TextWrapMode.WRAP)
    }
}