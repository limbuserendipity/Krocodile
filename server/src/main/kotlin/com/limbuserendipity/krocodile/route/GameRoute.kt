package com.limbuserendipity.krocodile.route

import com.limbuserendipity.krocodile.generateUniqueRandom
import com.limbuserendipity.krocodile.model.GameMessage
import com.limbuserendipity.krocodile.model.PlayerEvent
import com.limbuserendipity.krocodile.model.Room
import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.serialization.json.Json

fun Application.gameRoutes() {
    routing {
        webSocket("/game") {
            try {
                for (frame in incoming) {
                    when(frame){
                        is Frame.Text -> {
                            val message = Json.decodeFromString<GameMessage>(frame.readText())
                            handleMessage(this, message)
                        }
                        is Frame.Binary -> {}
                        is Frame.Close -> {}
                        is Frame.Ping -> {}
                        is Frame.Pong -> {}
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

private suspend fun handleMessage(session: DefaultWebSocketServerSession, message: GameMessage){
    when (message) {
        is GameMessage.PlayerMessage -> handlePlayerMessage(session, message.playerEvent)
        is GameMessage.Drawing -> {}
        is GameMessage.Guess -> {}
    }
}

private suspend fun handlePlayerMessage(session: DefaultWebSocketServerSession, event: PlayerEvent){
    when(event){
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
            if(room == null){
                event.player.roomId = Room.Lobby.id
                return
            }
            if(room.players.isNotEmpty()){
                if(room.artist == event.player){
                    room.artist = room.players.random()
                }
                if(room.owner == event.player){
                    room.owner = room.players.random()
                }
            }else{
                Room.Lobby.rooms.remove(event.player.roomId)
            }
            room.players.remove(event.player)
        }
    }
}