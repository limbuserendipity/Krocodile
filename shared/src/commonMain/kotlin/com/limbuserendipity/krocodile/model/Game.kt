package com.limbuserendipity.krocodile.model

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("drawing_event")
data class DrawingEvent(val x: Float, val y: Float, val isDrawing: Boolean)

@Serializable
@Polymorphic
sealed class GameMessage {

    @Serializable
    @SerialName("player_message")
    data class PlayerMessage(val playerEvent: PlayerEvent) : GameMessage()

    @Serializable
    @SerialName("server_message")
    data class ServerMessage(val serverStatus: ServerStatus) : GameMessage()

}

@Serializable
@Polymorphic
sealed class GameState{
    @Serializable
    @SerialName("wait")
    object Wait : GameState()
    @Serializable
    @SerialName("run")
    object Run : GameState()

}