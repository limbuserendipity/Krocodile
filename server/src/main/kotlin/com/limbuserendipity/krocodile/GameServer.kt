package com.limbuserendipity.krocodile

import com.limbuserendipity.krocodile.json.gameJsonModule
import com.limbuserendipity.krocodile.model.GameMessage
import com.limbuserendipity.krocodile.model.Player
import com.limbuserendipity.krocodile.model.PlayerEvent
import com.limbuserendipity.krocodile.model.Room
import com.limbuserendipity.krocodile.model.RoomData
import com.limbuserendipity.krocodile.model.ServerResult
import com.limbuserendipity.krocodile.model.ServerStatus
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import io.netty.util.internal.logging.Log4JLoggerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set

object GameServer {
    private val json = Json {
        serializersModule = gameJsonModule
        classDiscriminator = "type"
    }
    private val connections = ConcurrentHashMap<String, DefaultWebSocketServerSession>()
    private val scope = CoroutineScope(Dispatchers.Default)
    fun add(
        playerId: String,
        connection: DefaultWebSocketServerSession
    ) {
        connections.put(playerId, connection)
    }

    suspend fun handleMessage(session: DefaultWebSocketServerSession, text: String) {
        val message = json
            .decodeFromString<GameMessage>(text)
        when (message) {
            is GameMessage.PlayerMessage -> {
                handlePlayerMessage(session, message)
            }

            is GameMessage.Drawing -> {}
            is GameMessage.Guess -> {}
            is GameMessage.ServerMessage -> {}
        }
    }

    private suspend fun handlePlayerMessage(
        session: DefaultWebSocketServerSession,
        message: GameMessage
    ) {
        val event = (message as GameMessage.PlayerMessage).playerEvent

        val serverStatus : ServerStatus =
            when (event) {
                is PlayerEvent.NewPlayer -> {

                    val player = Player(
                        id = generatePlayerId(),
                        name = event.name,
                        isArtist = false,
                        roomId = Room.Lobby.id
                    )

                    add(player.id, session)

                    Room.Lobby.players.add(player)

                    val gameMessage = GameMessage.ServerMessage(
                        serverStatus = ServerStatus.Success(
                            result = ServerResult.PlayerState(player)
                        )
                    )
                    sendMessage(session, gameMessage)

                    ServerStatus.Success(
                        result = currentLobbyState()
                    )
                }

                is PlayerEvent.NewRoom -> {
                    val room = Room.GameRoom(
                        id = generateUniqueRandom(),
                        title = event.title,
                        players = mutableSetOf(event.player),
                        maxPlayers = event.maxPlayers,
                        owner = event.player,
                        artist = event.player
                    )
                    Room.Lobby.rooms[room.id] = room
                    Room.Lobby.players.first { it.id == event.player.id }.roomId = room.id

                    ServerStatus.Success(
                        currentLobbyState()
                    )
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

                    ServerStatus.Success(
                        result = currentLobbyState()
                    )
                }
            }

        val serverMessage = GameMessage.ServerMessage(
            serverStatus = serverStatus
        )
        broadcastMessage(serverMessage)
    }


    suspend fun sendMessage(session: DefaultWebSocketServerSession, message: GameMessage) {
        session.send(json.encodeToString(message))
    }


    private suspend fun broadcastMessage(serverMessage: GameMessage) {
        scope.launch {
            val message = json.encodeToString(serverMessage)
            val log = Log4JLoggerFactory.getDefaultFactory()

            connections.values.forEach { value ->
                value.send(Frame.Text(message))
            }
        }
    }
}

fun currentLobbyState(): ServerResult.LobbyState {
    return ServerResult.LobbyState(
        rooms = Room.Lobby.rooms.values.map { room ->
            RoomData(
                title = room.title,
                roomId = room.id,
                playerCount = room.players.count(),
                maxCount = room.maxPlayers
            )
        }
    )
}
