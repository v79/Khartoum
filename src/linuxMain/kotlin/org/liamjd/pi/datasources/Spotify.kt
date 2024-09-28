package org.liamjd.pi.datasources

import kotlinx.cinterop.ExperimentalForeignApi
import org.liamjd.pi.DisplayMode
import org.liamjd.pi.console.printDebug
import org.liamjd.pi.datasources.spotify.SpotifyService
import org.liamjd.pi.datasources.spotify.models.Item
import org.liamjd.pi.ePaper.EPDModel
import org.liamjd.pi.khartoum.KhFont
import org.liamjd.pi.khartoum.KhartoumImage
import org.liamjd.pi.khartoum.Rotation
import org.liamjd.pi.khartoum.TextWrapMode
import platform.posix.uint8_t

/**
 * Display the currently playing song on Spotify, showing Artist, Title and Album where appropriate, plus track number and total tracks
 */
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
                    red.drawString(
                        0,
                        0,
                        "Failed to refresh token",
                        KhFont.CascadiaMono12,
                        false,
                        wrapMode = TextWrapMode.WRAP
                    )
                } else {
                    val currentlyPlaying = spotify.getCurrentlyPlayingSong(refreshedToken, "GB")

                    if (currentlyPlaying != null) {
                        // TODO: Different displays for Tracks or Episodes
                        if (currentlyPlaying.item != null) {
                            when (currentlyPlaying.item) {
                                is Item.TrackObject -> {
                                    displayTrackInformation(currentlyPlaying.item)
                                }

                                is Item.EpisodeObject -> {
                                    displayEpisodeInformation(currentlyPlaying.item)
                                }
                            }

                        } else {
                            // currentlyPlaying.item is NULL; no song is playing
                            println("No song is playing")
                        }
                    }
                }
            } else {
                println("Spotify credentials not set")
                red.drawString(
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

    /**
     * Display music track information - title, albub, artist, track number
     * @param trackObject the track object to display
     */
    private fun displayTrackInformation(trackObject: Item.TrackObject) {
        println("${trackObject.name} by ${trackObject.artists?.firstOrNull()?.name} from the album ${trackObject.album?.name}. (Track ${trackObject.trackNumber} of ${trackObject.album?.totalTracks})")

        if (trackObject.name != null) {
            val drawnTitle = black.drawString(
                xStart = 0, yStart = 0,
                string = trackObject.name,
                font = KhFont.CascadiaCodeSemiBold24,
                wrapMode = TextWrapMode.TRUNCATE
            )
            if (trackObject.album?.name != null) {
                val drawnAlbum = red.drawString(
                    xStart = 0,
                    yStart = drawnTitle.y,
                    string = trackObject.album.name,
                    font = KhFont.CascadiaMono12,
                    wrapMode = TextWrapMode.TRUNCATE
                )
                black.drawString(
                    xStart = 0,
                    yStart = drawnAlbum.y,
                    string = "${trackObject.artists?.firstOrNull()?.name}",
                    font = KhFont.CascadiaMono12,
                    wrapMode = TextWrapMode.TRUNCATE
                )
            }
            trackObject.trackNumber?.let {
                black.drawString(
                    xStart = 0,
                    yStart = black.height - (KhFont.CascadiaMono12.height + 2),
                    string = "Track $it of ${trackObject.album?.totalTracks}",
                    font = KhFont.CascadiaMono12,
                    wrapMode = TextWrapMode.TRUNCATE
                )
            }
        }
    }

    /**
     * Display podcast episode information - title, show name, publisher, description
     * The description may be too long for the screen, so it may get truncated
     * @param episodeObject the episode object to display
     */
    private fun displayEpisodeInformation(episodeObject: Item.EpisodeObject) {
        println("${episodeObject.name} from ${episodeObject.show.name} published by ${episodeObject.show.publisher}.")

        val drawnTitle = black.drawString(
            xStart = 0, yStart = 0,
            string = episodeObject.name,
            font = KhFont.CascadiaCodeSemiBold24,
            wrapMode = TextWrapMode.TRUNCATE
        )
        episodeObject.show.name.let {
            val drawnShow = red.drawString(
                xStart = 0,
                yStart = drawnTitle.y,
                string = it,
                font = KhFont.CascadiaMono12,
                wrapMode = TextWrapMode.TRUNCATE
            )
            black.drawString(
                xStart = 0,
                yStart = drawnShow.y,
                string = "${episodeObject.show.publisher} / ${episodeObject.releaseDate}",
                font = KhFont.CascadiaMono12,
                wrapMode = TextWrapMode.TRUNCATE
            )
            // TODO: This might be too big for the screen, so need to be cleverer about how we display it
            // For now, I limit it to 100 characters
            println(episodeObject.description)
            val description = black.measureString(
                string = episodeObject.description.take(100),
                font = KhFont.CascadiaMono12, rotation = Rotation.CW, wrapMode = TextWrapMode.WRAP)
            println("Description dimensions are: $description")
            black.drawString(
                xStart = 0,
                yStart = black.height - ((KhFont.CascadiaMono12.height + 2) * description.textLines),
                string = episodeObject.description.take(100),
                font = KhFont.CascadiaMono12,
                wrapMode = TextWrapMode.WRAP
            )
        }
    }
}