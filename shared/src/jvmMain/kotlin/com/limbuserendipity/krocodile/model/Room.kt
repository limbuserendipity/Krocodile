package com.limbuserendipity.krocodile.model

import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

sealed class Room{

    object Lobby{
        val id : Long = 0
        val players = Collections.synchronizedSet(mutableSetOf<Player>())
        val rooms = ConcurrentHashMap<Long, GameRoom>()
    }

    data class GameRoom(
        val id : Long,
        val players : MutableSet<Player>,
        var owner : Player,
        var artist : Player,
    ): Room()
}