package org.liamjd.pi.datasources.spotify.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/* https://developer.spotify.com/documentation/web-api/reference/get-the-users-currently-playing-track */
/** An Item is either a Full Track object or a Full Episode object */
@Serializable
data class Item(
    @SerialName("album")
    val album: Album?,
    @SerialName("artists")
    val artists: List<SimpleArtist>?,
    @SerialName("available_markets")
    val availableMarkets: List<String>?,
    @SerialName("disc_number")
    val discNumber: Int?,
    @SerialName("duration_ms")
    val durationMs: Int?,
    @SerialName("explicit")
    val explicit: Boolean = false,
    @SerialName("external_ids")
    val externalIds: ExternalIds?,
    @SerialName("external_urls")
    val externalUrls: ExternalUrls?,
    @SerialName("href")
    val href: String?,
    @SerialName("id")
    val id: String?,
    @SerialName("is_local")
    val isLocal: Boolean = false,
    @SerialName("is_playable")
    val isPlayable: Boolean = true,
    @SerialName("linked_from")
    val linkedFrom: TrackLink? = null,
    @SerialName("name")
    val name: String?,
    @SerialName("popularity")
    val popularity: Int?,
    @SerialName("preview_url")
    val previewUrl: String?,
    @SerialName("restrictions")
    val restriction: Restriction? = null,
    @SerialName("track_number")
    val trackNumber: Int?,
    @SerialName("type")
    val type: String?,
    @SerialName("uri")
    val uri: String?
)
