package com.limbuserendipity.krocodile

import com.limbuserendipity.krocodile.model.GameMessage
import com.limbuserendipity.krocodile.model.PlayerEvent
import com.limbuserendipity.krocodile.model.Room
import io.ktor.server.websocket.DefaultWebSocketServerSession
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set

object GameServer {

    private val connections = ConcurrentHashMap<String, DefaultWebSocketServerSession>()

    suspend fun add(
        playerId : String,
        connection : DefaultWebSocketServerSession
    ) {
        connections.put(playerId, connection)
    }

    suspend fun handleMessage(session: DefaultWebSocketServerSession, message: GameMessage) {
        when (message) {
            is GameMessage.PlayerMessage -> handlePlayerMessage(session, message.playerEvent)
            is GameMessage.Drawing -> {}
            is GameMessage.Guess -> {}
        }
    }

    private suspend fun handlePlayerMessage(session: DefaultWebSocketServerSession, event: PlayerEvent) {
        when (event) {
            is PlayerEvent.NewPlayer -> {
                Room.Lobby.players.add(event.player)
            }

            is PlayerEvent.NewRoom -> {
                val room = Room.GameRoom(
                    id = generateUniqueRandom(),
                    players = mutableSetOf(event.player),
                    owner = event.player,
                    artist = event.player
                )
                Room.Lobby.rooms[room.id] = room
                event.player.roomId = room.id
            }

            is PlayerEvent.LeaveRoom -> {
                val room = Room.Lobby.rooms[event.player.roomId]
                if (room == null) {
                    event.player.roomId = Room.Lobby.id
                    return
                }
                if (room.players.isNotEmpty()) {
                    if (room.artist == event.player) {
                        room.artist = room.players.random()
                    }
                    if (room.owner == event.player) {
                        room.owner = room.players.random()
                    }
                } else {
                    Room.Lobby.rooms.remove(event.player.roomId)
                }
                room.players.remove(event.player)
            }
        }
    }

    suspend fun sendMessage() {

    }


    suspend fun broadcastMessage() {

    }
}

fun gameServer(
    scope: GameServer.() -> Unit
) {
    scope(GameServer)
}
