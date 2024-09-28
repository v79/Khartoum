package org.liamjd.pi.datasources.spotify.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Actions(
    @SerialName("disallows")
    val disallows: Disallows?
)
