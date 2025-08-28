package com.limbuserendipity.krocodile.model

import java.util.concurrent.ConcurrentHashMap

sealed class Room {

    object Lobby {
        val id: Long = 0
        val players = ConcurrentHashMap<String, Player>()
        val rooms = ConcurrentHashMap<Long, GameRoom>()
    }

    data class GameRoom(
        val id: Long,
        val title: String,
        val players: ConcurrentHashMap<String, Player>,
        val maxPlayers: Int,
        var owner: Player,
        var artist: Player,
        var state: GameState,
        var word: String,
        val chat: MutableList<ChatMessageData>,
        var round : Int
    ) : Room()
}