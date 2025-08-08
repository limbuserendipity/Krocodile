package com.limbuserendipity.krocodile.model

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@SerialName("player")
data class Player(val id: String, val name: String, var isArtist : Boolean, var roomId : Long)

@Serializable
@Polymorphic
sealed class PlayerEvent{
    @Serializable
    @SerialName("new_player")
    data class NewPlayer(val name : String) : PlayerEvent()
    @Serializable
    @SerialName("new_room")
    data class NewRoom(val player: Player) : PlayerEvent()
    @Serializable
    @SerialName("leave_room")
    data class LeaveRoom(val player: Player) : PlayerEvent()
}