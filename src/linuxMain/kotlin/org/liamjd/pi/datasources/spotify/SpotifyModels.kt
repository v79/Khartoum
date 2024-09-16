package org.liamjd.pi.spotify

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

 
@Serializable
data class AccessToken(
	@SerialName("access_token") val token: String,
	@SerialName("token_type") val type: String,
	@SerialName("expires_in") val expires: Int,
	val scope: String
)

