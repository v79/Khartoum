package org.liamjd.pi.datasources.spotify

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.liamjd.pi.console.printDebug
import org.liamjd.pi.curl.CUrl
import org.liamjd.pi.datasources.spotify.models.CurrentlyPlaying
import platform.posix.getenv

@ExperimentalForeignApi
class SpotifyService {

    private val spotifyClient: String
    private val spotifySecret: String
    private val spotifyRefreshToken: String
    private val spotifyAuthBytes: ByteArray
    private val spotifyAuth: String
    private val spotifyRedirectURL = "https://www.liamjd.org/spotCallback"

    private var token: AccessToken? = null

    init {
        spotifyClient = getEnvVariable("SPOTIFY_CLIENT")
        spotifySecret = getEnvVariable("SPOTIFY_SECRET")
        spotifyRefreshToken = getEnvVariable("SPOTIFY_REFRESH_TOKEN")
        println("Spotify client: $spotifyClient (secret, refresh not shown)")
        spotifyAuthBytes = "$spotifyClient:$spotifySecret".encodeToByteArray()
        spotifyAuth = spotifyAuthBytes.encodeBase64().toKString()
    }


    /**
     * @return true if client and secret environment variables are set, false otherwise
     */
    fun serviceIsValid(): Boolean {
        return spotifyClient.isNotEmpty() && spotifySecret.isNotEmpty()
    }

    // Refresh an access token using a stored refresh token.
    fun refreshAccessToken(): AccessToken? {
        val location = "https://accounts.spotify.com/api/token"
        val postData = "grant_type=refresh_token&refresh_token=$spotifyRefreshToken"
        val extraHeaders = arrayListOf(
            "Authorization: Basic $spotifyAuth",
            "Accept: application/json",
            "Content-Type: application/x-www-form-urlencoded"
        )

        var tokenJson = ""
        val curl = CUrl(url = location, extraHeaders = extraHeaders).apply {
            header += { if (it.startsWith("HTTP")) println("Response Status: $it") }
            body += { data -> tokenJson += data }
        }
        curl.post(data = postData)
        curl.close()

        return try {
            Json.decodeFromString<AccessToken>(tokenJson).also { token = it }
        } catch (e: Exception) {
            println("Error decoding refresh response: $tokenJson")
            println(e)
            null
        }
    }

    fun getCurrentlyPlayingSong(token: AccessToken, market: String): CurrentlyPlaying? {
        refreshAccessToken() ?: return null
        println("\tGetting currently playing song")
        val location =
            "https://api.spotify.com/v1/me/player/currently-playing?market=$market&additional_types=track%2Cepisode"
        val extraHeaders = arrayListOf(
            "Authorization: Bearer ${token.token}",
            "Accept: application/json",
            "Content-Type: application/json"
        )

        var currentlyPlayingJson = ""
        val curl = CUrl(url = location, extraHeaders = extraHeaders).apply {
            header += { if (it.startsWith("HTTP")) println("Response Status: $it") }
            body += { data ->
                currentlyPlayingJson += data
            }
        }
        curl.fetch()
        curl.close()

        if (currentlyPlayingJson.isEmpty()) {
            println("No currently playing song")
            return null
        }

        try {
            printDebug(currentlyPlayingJson)
            return Json.decodeFromString<CurrentlyPlaying>(currentlyPlayingJson)
        } catch (e: SerializationException) {
            println("Error: Could not decode currently playing JSON: $currentlyPlayingJson")
            println(e)
        }
        return null
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun getEnvVariable(varName: String): String {
        return getenv(varName)?.toKString().toString()
    }
}