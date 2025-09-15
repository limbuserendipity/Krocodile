package com.limbuserendipity.krocodile.model

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


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
    @SerialName("starting")
    object Starting : GameState()
    @Serializable
    @SerialName("run")
    object Run : GameState()

    @Serializable
    @SerialName("end")
    data class End(
        val winnerName: String,
        val guessedWord: String,
    ) : GameState()

}