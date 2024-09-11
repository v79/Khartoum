import kotlinx.cinterop.ExperimentalForeignApi
import org.liamjd.pi.khartoum.KhFont
import org.liamjd.pi.khartoum.KhartoumImage
import org.liamjd.pi.khartoum.Rotation
import org.liamjd.pi.khartoum.TextWrapMode
import kotlin.test.Test

class KhartoumImageTest {

    @OptIn(ExperimentalForeignApi::class)
    val image = KhartoumImage(0, 0)

    @OptIn(ExperimentalForeignApi::class)
    @Test
    fun `measure_text returns the correct width and height`() {
        val text = "This is a test"
        val font = KhFont.CascadiaCodeSemiBold24
        val dimensions = image.measureString(text, font, TextWrapMode.TRUNCATE, rotation = Rotation.CW)

    }
}