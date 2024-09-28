package org.liamjd.pi.datasources.spotify

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import kotlinx.serialization.json.Json
import org.liamjd.pi.console.printDebug
import org.liamjd.pi.curl.CUrl
import org.liamjd.pi.datasources.spotify.models.CurrentlyPlaying
import platform.posix.getenv

@ExperimentalForeignApi
class SpotifyService {

    private val spotifyClient: String
    private val spotifySecret: String
    private val spotifyAuthBytes: ByteArray
    private val spotifyAuth: String
    private val spotifyRedirectURL = "https://www.liamjd.org/spotCallback"

    private var token: AccessToken? = null

    init {
        spotifyClient = getEnvVariable("SPOTIFY_CLIENT")
        spotifySecret = getEnvVariable("SPOTIFY_SECRET")
        println("Spotify client: $spotifyClient (secret, auth not shown)")
        spotifyAuthBytes = "$spotifyClient:$spotifySecret".encodeToByteArray()
        spotifyAuth = spotifyAuthBytes.encodeBase64().toKString()
    }

    // TODO: These values are hard coded and specific to my Spotify account. They should be removed
    // See https://dev.to/sabareh/how-to-get-the-spotify-refresh-token-176 for instructions on how these values are fetched
    private val spotifyCode =
        "AQAlR_EO4GCNPBuRrc2jCHZaaLkdOfX2V9b7tcNwTCPPtv1HLICIPsDtaCwUyD4tqrG5VxOOe3dpIrxRbn1KeWAT38dlwFnZWIJQ7CcHqNNUg2mSci7iGDms9Gwc97v9brHA6wSceEbuKa2vS1MVV5rukCxrJqG5Ktj5b_Ji0hffsppCR9LhjgG4R-MTLg_B3-rm69ldnSk"
    private val spotifyRefreshToken =
        "AQA0BxCGAD4MVEzsHQT58gQR0iZ021biAzrnrawXs_sRLbNLLuTsFgqojrH9xC0RKhsLOR__3sU6msgPA6VJt14YYiAB_3D2xBaXPY5HwSdW75yUv1SMVFT95kyBIG9ILYE"


    /**
     * @return true if client and secret environment variables are set, false otherwise
     */
    fun serviceIsValid(): Boolean {
        return spotifyClient.isNotEmpty() && spotifySecret.isNotEmpty()
    }

    /**
     * Given a refresh token, refresh the Spotify access token
     */
    fun refreshSpotifyToken(): AccessToken? {
        require(spotifyClient.isNotEmpty()) { "Spotify client ID is empty" }
        require(spotifySecret.isNotEmpty()) { "Spotify secret is empty" }

        if (token == null) {
            getSpotifyToken()
        }

        val location = "https://accounts.spotify.com/api/token"
        val extraHeaders = arrayListOf(
            "Authorization: Basic $spotifyAuth",
            "Accept: application/json",
            "Content-Type: application/x-www-form-urlencoded"
        )
        val postData = "grant_type=refresh_token&refresh_token=$spotifyRefreshToken"
        var refreshToken = ""

        val curl = CUrl(url = location, extraHeaders = extraHeaders).apply {
            header += { if (it.startsWith("HTTP")) println("Response Status: $it") }
            body += { data ->
                refreshToken = data
            }
        }
        curl.post(data = postData)
        curl.close()

        try {
            println("\tRefreshed token: $refreshToken")
            token = Json.decodeFromString<AccessToken>(refreshToken)
        } catch (e: Exception) {
            println(e)
        }
        return token
    }

    /**
     * Get initial spotify access token
     */
    private fun getSpotifyToken() {
        val location = "https://accounts.spotify.com/api/token"
        val postData = "grant_type=client_credentials"
        val extraHeaders = arrayListOf(
            "Authorization: Basic $spotifyAuth",
            "Accept: application/json",
            "Content-Type: application/x-www-form-urlencoded"
        )
        var accessTokenJson = ""
        val curl = CUrl(url = location, extraHeaders = extraHeaders).apply {
            header += { if (it.startsWith("HTTP")) println("Response Status: $it") }
            body += { data ->
                accessTokenJson += data
            }
        }
        curl.post(data = postData)
        curl.close()

        try {
            printDebug("\tAccess token: $accessTokenJson")
            token = Json.decodeFromString<AccessToken>(accessTokenJson)
        } catch (e: Exception) {
            println(e)
        }
    }

    fun getCurrentlyPlayingSong(token: AccessToken, market: String): CurrentlyPlaying? {
        println("\tGetting currently playing song")
        val location = "https://api.spotify.com/v1/me/player/currently-playing?market=$market&additional_types=track%2Cepisode"
        val extraHeaders = arrayListOf(
            "Authorization: Bearer ${token.token}",
            "Accept: application/json",
            "Content-Type: application/json"
        )

        var currentlyPlayingJson: String = ""
        val curl = CUrl(url = location, extraHeaders = extraHeaders).apply {
            header += { if (it.startsWith("HTTP")) println("Response Status: $it") }
            body += { data ->
                currentlyPlayingJson += data
            }
        }
        curl.fetch()
        curl.close()

        if(currentlyPlayingJson.isEmpty()) {
            return null
        }

        try {
            printDebug(currentlyPlayingJson)
            return Json.decodeFromString<CurrentlyPlaying>(currentlyPlayingJson)
        } catch (e: Exception) {
            println(e)
            println(currentlyPlayingJson)
        }
        return null
    }

    private fun getSpotifyAuthScope(): String {
        val location = "https://accounts.spotify.com/api/token"
        val postData = "grant_type=authorization_code&code=$spotifyCode&redirect_uri=$spotifyRedirectURL"
        val extraHeaders = arrayListOf(
            "Authorization: Basic $spotifyAuth",
            "Accept: application/json",
            "Content-Type: application/x-www-form-urlencoded"
        )
        var authJson = ""

        val curl = CUrl(url = location, extraHeaders = extraHeaders).apply {
            header += { if (it.startsWith("HTTP")) println("Response Status: $it") }
            body += { data ->
                authJson += data
            }
        }
        curl.post(data = postData)
        curl.close()

        return authJson
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun getEnvVariable(varName: String): String {
        return getenv(varName)?.toKString().toString()
    }
}