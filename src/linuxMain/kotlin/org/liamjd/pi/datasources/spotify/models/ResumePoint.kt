package org.liamjd.pi.datasources.spotify.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResumePoint(
    @SerialName("fully_played")
    val fullyPlayed: Boolean,
    @SerialName("resume_position_ms")
    val resumePositionMs: Int
)
