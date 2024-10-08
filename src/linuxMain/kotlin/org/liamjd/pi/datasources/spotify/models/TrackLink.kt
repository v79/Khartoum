package org.liamjd.pi.datasources.spotify.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.liamjd.pi.datasources.spotify.models.ExternalUrls

@Serializable
class TrackLink(
    @SerialName("external_urls")
	val externalUrls: ExternalUrls,
    @SerialName("href")
	val href: String?,
    @SerialName("id")
	val id: String,
    @SerialName("type")
	val type: String = "track",
    @SerialName("uri")
	val uri: String?
)
