package com.limbuserendipity.krocodile.model

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@SerialName("player")
data class Player(val id: String, val name: String, var isArtist: Boolean, var roomId: Long)

@Serializable
@Polymorphic
sealed class PlayerEvent {
    @Serializable
    @SerialName("new_player")
    data class NewPlayer(val name: String) : PlayerEvent()

    @Serializable
    @SerialName("new_room")
    data class NewRoom(val player: Player, val title: String, val maxPlayers: Int) : PlayerEvent()

    @Serializable
    @SerialName("leave_room")
    data class LeaveRoom(val player: Player) : PlayerEvent()
    @Serializable
    @SerialName("enter_to_room")
    data class EnterToRoom(
        val player: Player,
        val roomId: Long
    ) : PlayerEvent()

    @Serializable
    @SerialName("start_game")
    data class StartGame(
        val player: Player
    ) : PlayerEvent()

    @Serializable
    @SerialName("word")
    data class Word(
        val player : Player,
        val word : String
    ) : PlayerEvent()

    @Serializable
    @SerialName("chat_message")
    data class ChatMessage(
        val player: Player,
        val message: String
    ) : PlayerEvent()

    @Serializable
    @SerialName("drawing")
    data class Drawing(
        val player: Player,
        val pathData: PathData
    ) : PlayerEvent()

}

@Serializable
@SerialName("player_data")
data class PlayerData(
    val id: String,
    val name: String
)

@Serializable
@SerialName("chat_message_data")
data class ChatMessageData(
    val playerName : String,
    val message : String
)