package com.limbuserendipity.krocodile.client.state

import com.limbuserendipity.krocodile.model.*

data class ClientState(
    val player: Player? = null,
    val currentRoom: RoomData? = null,
    val lobbyRooms: List<RoomData> = emptyList(),
    val roomPlayers: List<PlayerData> = emptyList(),
    val chatMessages: List<ChatMessageData> = emptyList(),
    val availableWords: List<String> = emptyList(),
    val isArtist: Boolean = false,
    val connectionStatus: ConnectionStatus = ConnectionStatus.Disconnected,
    val owner: PlayerData? = null,
    val artist: PlayerData? = null,
    val round: Int = 0,
    val drawingEvent: DrawingEvent? = null
)

enum class ConnectionStatus {
    Disconnected, Connecting, Connected, Error
}