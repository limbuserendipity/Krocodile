package com.limbuserendipity.krocodile.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("path_data")
data class PathData(
    val relativeX: Float,
    val relativeY: Float,
    val color: ULong,
    val normalizedSize: Float,
    val drawerId: String,
    val drawState: DrawState
)

@Serializable
@SerialName("path_state")
sealed class DrawState {
    @Serializable
    @SerialName("draw_start")
    object DrawStart : DrawState()

    @Serializable
    @SerialName("drawing")
    object Drawing : DrawState()

    @Serializable
    @SerialName("draw_end")
    object DrawEnd : DrawState()

}