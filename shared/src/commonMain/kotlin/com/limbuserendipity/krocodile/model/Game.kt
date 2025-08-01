package com.limbuserendipity.krocodile.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DrawingEvent(val x: Float, val y: Float, val isDrawing: Boolean)

@Serializable
sealed class GameMessage {

    @Serializable
    @SerialName("player")
    data class PlayerMessage(val playerEvent: PlayerEvent) : GameMessage()

    @Serializable
    @SerialName("draw")
    data class Drawing(val event: DrawingEvent) : GameMessage()

    @Serializable
    @SerialName("guess")
    data class Guess(val text: String) : GameMessage()

}