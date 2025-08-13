package com.limbuserendipity.krocodile

import com.limbuserendipity.krocodile.json.gameJsonModule
import com.limbuserendipity.krocodile.model.GameMessage
import com.limbuserendipity.krocodile.model.GameState
import com.limbuserendipity.krocodile.model.Player
import com.limbuserendipity.krocodile.model.PlayerData
import com.limbuserendipity.krocodile.model.PlayerEvent
import com.limbuserendipity.krocodile.model.Room
import com.limbuserendipity.krocodile.model.RoomData
import com.limbuserendipity.krocodile.model.ServerResult
import com.limbuserendipity.krocodile.model.ServerStatus
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.websocket.Frame
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
    fun addSession(
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

        when (event) {
            is PlayerEvent.NewPlayer -> {

                val player = Player(
                    id = generatePlayerId(),
                    name = event.name,
                    isArtist = false,
                    roomId = Room.Lobby.id
                )

                addSession(player.id, session)

                Room.Lobby.players[player.id] = player

                updatePlayerState(player)

            }

            is PlayerEvent.NewRoom -> {
                val id = generateUniqueRandom()
                Room.Lobby.players[event.player.id]?.let { player ->
                    Room.GameRoom(
                        id = id,
                        title = event.title,
                        players = ConcurrentHashMap(),
                        maxPlayers = event.maxPlayers,
                        owner = player,
                        artist = player,
                        state = GameState.Wait,
                        word = ""
                    ).also { room ->
                        player.roomId = id
                        room.players.put(player.id, player)
                        Room.Lobby.rooms[room.id] = room
                        Room.Lobby.players.remove(player.id)
                        updateRoomState(room)
                        updatePlayerState(player)
                    }
                }

            }

            is PlayerEvent.LeaveRoom -> {
                val room = Room.Lobby.rooms[event.player.roomId]
                if (room == null) {
                    event.player.roomId = Room.Lobby.id
                    return
                }
                if (room.players.isNotEmpty()) {
                    if (room.artist == event.player) {
                        val artist = room.players.values.random()
                        artist.isArtist = true
                        room.artist = artist
                        room.state = GameState.Wait
                    }
                    if (room.owner == event.player) {
                        val owner = room.players.values.random()
                        room.owner = owner
                    }
                } else {
                    Room.Lobby.rooms.remove(event.player.roomId)

                }
                room.players.remove(event.player.id)
                updateRoomState(room)
                updatePlayerState(event.player)
            }

            is PlayerEvent.EnterToRoom -> {
                Room.Lobby.players[event.player.id]?.let { player ->
                    val room = Room.Lobby.rooms.getOrElse(event.roomId) {
                        throw Exception("Room d`t exist")
                    }

                    Room.Lobby.rooms[event.roomId]?.players?.put(player.id, player)
                    Room.Lobby.players[event.player.id] = player.copy(roomId = event.roomId)
                    if (room.players.count() == room.maxPlayers) {
                        runGameInRoom(room)
                    }

                    updateRoomState(room)
                    updatePlayerState(player)
                }
            }

            is PlayerEvent.Word -> {
                println("word")
                val room = Room.Lobby.rooms[event.player.roomId]
                if (room != null) {
                    println("room not null")
                    room.word = event.word
                    room.state = GameState.Run
                    updateRoomState(room)
                }
            }
        }

        updateLobbyState()

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

    suspend fun updateRoomState(room: Room.GameRoom) {
        val gameMessage = GameMessage.ServerMessage(
            serverStatus = ServerStatus.Success(
                result = ServerResult.RoomState(
                    roomData = RoomData(
                        title = room.title,
                        roomId = room.id,
                        playerCount = room.players.count(),
                        maxCount = room.maxPlayers
                    ),
                    players = room.players.values.map { player ->
                        PlayerData(
                            id = player.id,
                            name = player.name
                        )
                    },
                    owner = room.owner.let { player ->
                        PlayerData(
                            id = player.id,
                            name = player.name
                        )
                    },
                    artist = room.artist.let { player ->
                        PlayerData(
                            id = player.id,
                            name = player.name
                        )
                    },
                    gameState = room.state
                )
            )
        )

        room.players.values.forEach { player ->
            connections[player.id]?.let { session ->
                sendMessage(session, gameMessage)
            }
        }
    }

    suspend fun updatePlayerState(player: Player) {
        val gameMessage = GameMessage.ServerMessage(
            serverStatus = ServerStatus.Success(
                result = ServerResult.PlayerState(player)
            )
        )
        connections[player.id]?.let { session ->
            sendMessage(session, gameMessage)
        }
    }

    suspend fun updateLobbyState() {
        val serverStatus = ServerStatus.Success(
            result = currentLobbyState()
        )

        val serverMessage = GameMessage.ServerMessage(
            serverStatus = serverStatus
        )

        Room.Lobby.players.keys.forEach { id ->
            connections[id]?.let {
                sendMessage(it, serverMessage)
            }
        }

    }

    suspend fun updateWords(
        artist: Player
    ) {
        val gameMessage = GameMessage.ServerMessage(
            serverStatus = ServerStatus.Success(
                result = ServerResult.Words(words = words)
            )
        )
        sendMessage(
            session = connections[artist.id]!!,
            message = gameMessage
        )
    }


    suspend fun runGameInRoom(room: Room.GameRoom) {
        if (room.players.count() == room.maxPlayers) {
//            val artist = room.players.values.random()
//            artist.isArtist = true
//            room.artist = artist
            updateRoomState(room)
            updateWords(room.artist)
        } else {
            room.state = GameState.Wait
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

val words = listOf(
    "Tree",
    "Home",
    "Car"
)