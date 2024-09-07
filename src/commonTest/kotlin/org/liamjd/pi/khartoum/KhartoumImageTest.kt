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
}