package org.liamjd.pi.datasources.spotify.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/* https://developer.spotify.com/documentation/web-api/reference/get-the-users-currently-playing-track */
@Serializable
data class CurrentlyPlaying(
    @SerialName("actions")
    val actions: Actions?,
    @SerialName("context")
    val context: Context?,
    @SerialName("currently_playing_type")
    val currentlyPlayingType: String?,
    @SerialName("is_playing")
    val isPlaying: Boolean? = true,
    @SerialName("item")
    val item: Item?, // Can either be a TrackObject or an EpisodeObject, or null
    @SerialName("progress_ms")
    val progressMs: Int? = 0,
    @SerialName("timestamp")
    val timestamp: Long?
)
