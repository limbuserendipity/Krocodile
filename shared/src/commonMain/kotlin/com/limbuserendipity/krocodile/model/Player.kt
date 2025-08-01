package com.limbuserendipity.krocodile.model

import kotlinx.serialization.Serializable


@Serializable
data class Player(val id: String, val name: String, var isArtist : Boolean, var roomId : Long)

@Serializable
sealed class PlayerEvent{
    @Serializable
    data class NewPlayer(val player: Player) : PlayerEvent()
    @Serializable
    data class NewRoom(val player: Player) : PlayerEvent()
    @Serializable
    data class LeaveRoom(val player: Player) : PlayerEvent()
}