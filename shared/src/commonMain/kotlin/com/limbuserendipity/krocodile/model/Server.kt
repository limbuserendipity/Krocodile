package com.limbuserendipity.krocodile.model

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@Polymorphic
sealed class ServerResult {
    @Serializable
    @SerialName("lobby_state")
    data class LobbyState(
        val playerCount: Int,
        val rooms: List<Long>
    ) : ServerResult()

    @Serializable
    @SerialName("room_state")
    data class RoomState(
        val players: List<Player>,
    ) : ServerResult()

    @Serializable
    @SerialName("player_state")
    data class PlayerState(
        val player : Player
    ) : ServerResult()

}

@Serializable
@Polymorphic
sealed class ServerStatus {
    @Serializable
    @SerialName("success")
    data class Success(val message: GameMessage, val result: ServerResult) : ServerStatus()

    @Serializable
    @SerialName("error")
    class Error : ServerStatus()

}
