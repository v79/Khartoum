package org.liamjd.pi.datasources.spotify.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Copyright(
	@SerialName("text")
	val text: String,
	@SerialName("type")
	val type: String
)
