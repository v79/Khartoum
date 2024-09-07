package org.liamjd.pi.khartoum

import kotlin.test.Test

@OptIn(ExperimentalUnsignedTypes::class)
class KhartoumImageTest {

    @Test
    fun testReset() {
        val khartoumImage = KhartoumImage(0, 0)
        khartoumImage.reset(Rotation.CW)
    }

    @Test
    fun testDrawString() {
        val khartoumImage = KhartoumImage(0, 0)
        khartoumImage.drawString(0, 0, "test", KhFont.CascadiaCodeSemiBold24, false, TextWrapMode.WRAP)
    }
}