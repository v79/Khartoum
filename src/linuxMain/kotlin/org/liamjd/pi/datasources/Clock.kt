package org.liamjd.pi.datasources

import kotlinx.datetime.*
import kotlinx.datetime.Clock
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import org.liamjd.pi.DisplayMode
import org.liamjd.pi.ePaper.EPDModel
import org.liamjd.pi.khartoum.KhFont
import org.liamjd.pi.khartoum.KhartoumImage
import org.liamjd.pi.khartoum.Rotation
import org.liamjd.pi.khartoum.TextWrapMode
import platform.posix.uint8_t

/**
 * Display the current date and time
 */
class Clock(override val id: uint8_t = 6u) : DisplayMode {
    private var black: KhartoumImage = KhartoumImage(0, 0)
    private var red: KhartoumImage = KhartoumImage(0, 0)

    override fun toString(): String {
        return "Clock"
    }

    override val images
        get() = arrayOf(black.bytes, red.bytes)

    override fun refresh(ePaperModel: EPDModel) {

        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        println(now)

        val timeFormat = LocalDateTime.Format {
            hour(padding = Padding.ZERO)
            char(':')
            minute(padding = Padding.ZERO)
        }

        val formattedTime = now.format(timeFormat)

        val dateFormat = LocalDateTime.Format {
            dayOfWeek(names = DayOfWeekNames.ENGLISH_ABBREVIATED)
            char(' ')
            dayOfMonth()
            char(' ')
            monthName(names = MonthNames.ENGLISH_FULL)
            char(' ')
            year(padding = Padding.NONE)
        }

        val formattedDate = now.format(dateFormat)
        println("$formattedDate $formattedTime")

        black = KhartoumImage(ePaperModel.pixelWidth, ePaperModel.pixelHeight)
        red = KhartoumImage(ePaperModel.pixelWidth, ePaperModel.pixelHeight)
        black.reset(Rotation.CW)
        red.reset(Rotation.CW)
        black.drawString(1, 1, formattedTime, KhFont.CascadiaCodeSemiBold24, false, wrapMode = TextWrapMode.TRUNCATE)
        red.drawString(1,50, formattedDate, KhFont.CascadiaCodeSemiBold24, false, wrapMode = TextWrapMode.WRAP)
    }
}