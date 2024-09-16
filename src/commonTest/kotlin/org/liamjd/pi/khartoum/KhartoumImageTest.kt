package org.liamjd.pi.khartoum

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


@OptIn(ExperimentalUnsignedTypes::class)
class KhartoumImageTest {

    @Test
    fun `create a zero size image`() {
        val khartoumImage = KhartoumImage(0, 0)
        khartoumImage.reset(Rotation.CW)

        assertTrue(khartoumImage.width == 0)
    }

    @Test
    fun `test measuring a string`() {
        val message = "Shutdown"
        println("test width ~ ${message.length * KhFont.CascadiaCodeSemiBold24.width}")
        val khartoumImage = KhartoumImage(200, 200)
        val dimensions =
            khartoumImage.measureString(message, KhFont.CascadiaCodeSemiBold24, TextWrapMode.WRAP, Rotation.NONE)
        assertTrue(dimensions.w > 0, "width is ${dimensions.w}")
        assertTrue(dimensions.w < 200, "width is ${dimensions.w}")
    }

    @Test
    fun `returns dimensions of a string that fits on one line`() {
        val message = "Shutdown"
        val khartoumImage = KhartoumImage(200, 200)
        val dimensions =
            khartoumImage.measureString(message, KhFont.CascadiaCodeSemiBold24, TextWrapMode.WRAP, Rotation.NONE)
        println(dimensions)
        assertTrue(dimensions.w > 0, "width is ${dimensions.w}")
        assertTrue(dimensions.w < 200, "width is ${dimensions.w}")
        assertTrue(dimensions.h < 40, "height is ${dimensions.h}")
        assertEquals(dimensions.textLines, 1)
    }

    @Test
    fun `returns dimensions of a string that wraps to two lines`() {
        val message = "Shutdown the system"
        val khartoumImage = KhartoumImage(200, 200)
        val dimensions =
            khartoumImage.measureString(message, KhFont.CascadiaCodeSemiBold24, TextWrapMode.WRAP, Rotation.NONE)
        println(dimensions)
        assertTrue(dimensions.w > 0, "width is ${dimensions.w}")
        assertTrue(dimensions.w == 200, "width is ${dimensions.w}")
        assertTrue(dimensions.h > 40, "height is ${dimensions.h}")
        assertEquals(dimensions.textLines, 2)
    }

    @Test
    fun `can centre a short string on a single line`() {
        val message = "Shutdown"
        val khartoumImage = KhartoumImage(200, 200)
        val dimensions =
            khartoumImage.measureString(message, KhFont.CascadiaCodeSemiBold24, TextWrapMode.WRAP, Rotation.NONE)
        println("Measured: $dimensions")
        val drawnDimensions =
            khartoumImage.centreString(message, KhFont.CascadiaCodeSemiBold24, TextWrapMode.WRAP, Rotation.NONE)
        println("Drawn: $drawnDimensions")
        val expectedXStart = (200 - 152) / 2
        assertEquals(drawnDimensions.x, expectedXStart, "Drew at x=${drawnDimensions.x}, expected $expectedXStart")
        assertEquals(drawnDimensions.h, 34, "Height is ${drawnDimensions.h}, expected 34")
        assertEquals(drawnDimensions.textLines, 1, "Expected 1 line, got ${drawnDimensions.textLines}")
    }

    @Test
    fun `can centre short text vertically only`() {
        val message = "Vertical"
        val khartoumImage = KhartoumImage(200, 200)
        val dimensions =
            khartoumImage.measureString(message, KhFont.CascadiaCodeSemiBold24, TextWrapMode.WRAP, Rotation.NONE)
        println("Measured: $dimensions")
        val drawDimensions =
            khartoumImage.centreString(
                message,
                KhFont.CascadiaCodeSemiBold24,
                TextWrapMode.WRAP,
                Rotation.NONE,
                horizontal = false,
                vertical = true
            )
        println("Drawn: $drawDimensions")

        assertTrue(drawDimensions.x == 0, "x is ${drawDimensions.x}")
        assertTrue(drawDimensions.y > 0, "y is ${drawDimensions.y}")
    }
}


