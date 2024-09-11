package org.liamjd.pi.khartoum

import kotlin.test.Test
import kotlin.test.assertTrue


@OptIn(ExperimentalUnsignedTypes::class)
class KhartoumImageTest {

    @Test
    fun testReset() {
        val khartoumImage = KhartoumImage(0, 0)
        khartoumImage.reset(Rotation.CW)

        assertTrue(khartoumImage.width == 0)
    }

    @Test
    fun testDrawString() {
        val khartoumImage = KhartoumImage(200, 200)
        khartoumImage.drawString(0, 0, "test", KhFont.CascadiaCodeSemiBold24, false, TextWrapMode.WRAP)
    }

    @Test
    fun `test measuring a string`() {
        val message = "Shutdown"
        println("test width ~ ${message.length * KhFont.CascadiaCodeSemiBold24.width}")
        val khartoumImage = KhartoumImage(200, 200)
        val dimensions = khartoumImage.measureString(message, KhFont.CascadiaCodeSemiBold24, TextWrapMode.WRAP, Rotation.NONE)
        assertTrue(dimensions.x > 0, "x dimension is ${dimensions.x}")
        assertTrue(dimensions.x < 200, "x dimension is ${dimensions.x}")
    }
}


