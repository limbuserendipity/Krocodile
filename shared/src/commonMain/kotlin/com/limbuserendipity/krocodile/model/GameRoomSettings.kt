package com.limbuserendipity.krocodile.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("game_room_settings")
data class GameRoomSettings(
    var maxPlayers: Int,
    var maxRounds : Int,
    var maxTime : Int,
    val difficulty : GameDifficulty
)

fun gameRoomSettingsPlaceholder() = GameRoomSettings(
    maxPlayers = 2,
    maxRounds = 5,
    maxTime = 60,
    difficulty = GameDifficulty.NORMAL
)