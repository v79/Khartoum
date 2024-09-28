package org.liamjd.pi.datasources.spotify.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Restriction(
	@SerialName("reason")
	val reason: String
)
