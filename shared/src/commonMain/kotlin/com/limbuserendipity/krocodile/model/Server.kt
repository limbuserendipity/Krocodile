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
        val rooms: List<RoomData>
    ) : ServerResult()

    @Serializable
    @SerialName("room_state")
    data class RoomState(
        val roomData: RoomData,
        val players: List<PlayerData>,
        var owner: PlayerData,
        var artist: PlayerData,
        val chat: List<ChatMessageData>,
        val round: Int
    ) : ServerResult()

    @Serializable
    @SerialName("player_state")
    data class PlayerState(
        val player: Player
    ) : ServerResult()

    @Serializable
    @SerialName("words")
    data class Words(
        val words: List<String>
    ) : ServerResult()

    @Serializable
    @SerialName("drawing")
    data class DrawingState(
        val playerId: String,
        val drawingEvent: DrawingEvent
    ) : ServerResult()

    @Serializable
    @SerialName("notification")
    data class Notification(
        val message: NotificationMessage
    ) : ServerResult()

}

@Serializable
@Polymorphic
sealed class ServerStatus {
    @Serializable
    @SerialName("success")
    data class Success(val result: ServerResult) : ServerStatus()

    @Serializable
    @SerialName("error")
    data class Error(val message: String) : ServerStatus()

}

@Serializable
@SerialName("room_data")
data class RoomData(
    val title: String,
    val roomId: Long,
    val playerCount: Int,
    val maxCount: Int,
    val gameState: GameState,
)