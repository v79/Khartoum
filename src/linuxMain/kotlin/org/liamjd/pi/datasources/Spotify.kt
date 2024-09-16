package org.liamjd.pi.datasources

import kotlinx.cinterop.ExperimentalForeignApi
import org.liamjd.pi.DisplayMode
import org.liamjd.pi.datasources.spotify.SpotifyService
import org.liamjd.pi.ePaper.EPDModel
import org.liamjd.pi.khartoum.KhFont
import org.liamjd.pi.khartoum.KhartoumImage
import org.liamjd.pi.khartoum.Rotation
import org.liamjd.pi.khartoum.TextWrapMode
import platform.posix.uint8_t

@OptIn(ExperimentalForeignApi::class)
class Spotify(override val id: uint8_t = 6u) : DisplayMode {
    private var black: KhartoumImage = KhartoumImage(0, 0)
    private var red: KhartoumImage = KhartoumImage(0, 0)
    val spotify = SpotifyService()

    override fun toString(): String {
        return "Spotify"
    }

    override val images
        get() = arrayOf(black.bytes, red.bytes)


    override fun refresh(ePaperModel: EPDModel) {
        black = KhartoumImage(ePaperModel.pixelWidth, ePaperModel.pixelHeight)
        red = KhartoumImage(ePaperModel.pixelWidth, ePaperModel.pixelHeight)
        black.reset(Rotation.CW)
        red.reset(Rotation.CW)

        try {
            if (spotify.serviceIsValid()) {
                val refreshedToken = spotify.refreshSpotifyToken()
                if (refreshedToken == null) {
                    println("Failed to refresh token")
                    black.drawString(
                        0,
                        0,
                        "Failed to refresh token",
                        KhFont.CascadiaMono12,
                        false,
                        wrapMode = TextWrapMode.WRAP
                    )
                } else {
                    val currentlyPlaying = spotify.getCurrentlyPlayingSong(refreshedToken)

                    if (currentlyPlaying != null) {
                        println("${currentlyPlaying.item.name} by ${currentlyPlaying.item.artists?.firstOrNull()?.name} from the album ${currentlyPlaying.item.album?.name}. (Track ${currentlyPlaying.item.trackNumber} of ${currentlyPlaying.item.album?.totalTracks})")

                        if (currentlyPlaying.item.name != null) {
                            val drawnTitle = black.drawString(
                                xStart = 0, yStart = 0,
                                string = currentlyPlaying.item.name,
                                font = KhFont.CascadiaCodeSemiBold24,
                                wrapMode = TextWrapMode.TRUNCATE
                            )
                            println("Title dimensions are: $drawnTitle")

                            if (currentlyPlaying.item.album?.name != null) {
                                val drawnAlbum = red.drawString(
                                    xStart = 0,
                                    yStart = drawnTitle.y,
                                    string = currentlyPlaying.item.album.name,
                                    font = KhFont.CascadiaMono12,
                                    wrapMode = TextWrapMode.TRUNCATE
                                )
                                println("Album dimensions are: $drawnAlbum")
                                black.drawString(
                                    xStart = 0,
                                    yStart = drawnAlbum.y,
                                    string = "${currentlyPlaying.item.artists?.firstOrNull()?.name}",
                                    font = KhFont.CascadiaMono12,
                                    wrapMode = TextWrapMode.TRUNCATE
                                )
                            }
                        }
                    }
                }
            } else {
                println("Spotify credentials not set")
                black.drawString(
                    0,
                    0,
                    "Spotify credentials not set",
                    KhFont.CascadiaMono12,
                    false,
                    wrapMode = TextWrapMode.WRAP
                )
            }
        } catch (e: Exception) {
            println("Caught exception: $e")
            println(e.stackTraceToString())
        }

    }
}