package org.liamjd.pi.datasources.spotify.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Image(
    @SerialName("height")
    val height: Int?,
    @SerialName("url")
    val url: String?,
    @SerialName("width")
    val width: Int?
)
