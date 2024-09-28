package org.liamjd.pi.datasources.spotify.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.liamjd.pi.datasources.spotify.models.ExternalUrls

@Serializable
data class SimpleArtist(
    @SerialName("external_urls")
    val externalUrls: ExternalUrls?,
    @SerialName("href")
    val href: String?,
    @SerialName("id")
    val id: String?,
    @SerialName("name")
    val name: String,
    @SerialName("type")
    val type: String?,
    @SerialName("uri")
    val uri: String?
)
