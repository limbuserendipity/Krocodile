import com.limbuserendipity.krocodile.model.*
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class MessageSender(
    private val connectionManager: ConnectionManager,
    private val json: Json
) {

    suspend fun sendRoomState(room: Room.GameRoom) {
        val gameMessage = GameMessage.ServerMessage(
            serverStatus = ServerStatus.Success(
                result = ServerResult.RoomState(
                    roomData = RoomData(
                        title = room.title,
                        roomId = room.id,
                        playerCount = room.players.count(),
                        maxCount = room.maxPlayers,
                        gameState = room.state
                    ),
                    players = room.players.values.map { player ->
                        PlayerData(
                            id = player.id,
                            name = player.name
                        )
                    },
                    owner = PlayerData(
                        id = room.owner.id,
                        name = room.owner.name
                    ),
                    artist = PlayerData(
                        id = room.artist.id,
                        name = room.artist.name
                    ),
                    chat = room.chat,
                    round = room.round
                )
            )
        )

        connectionManager.broadcastToRoom(room, gameMessage, json)
    }

    suspend fun sendPlayerState(player: Player) {
        val gameMessage = GameMessage.ServerMessage(
            serverStatus = ServerStatus.Success(
                result = ServerResult.PlayerState(player)
            )
        )
        connectionManager.sendToPlayer(player.id, gameMessage, json)
    }

    suspend fun sendPlayerStateToRoom(room: Room.GameRoom) {
        room.players.values.forEach { player ->
            val gameMessage = GameMessage.ServerMessage(
                serverStatus = ServerStatus.Success(
                    result = ServerResult.PlayerState(player)
                )
            )
            connectionManager.sendToPlayer(player.id, gameMessage, json)
        }
    }

    suspend fun sendLobbyState(lobbyPlayers: ConcurrentHashMap<String, Player>) {
        val serverStatus = ServerStatus.Success(
            result = ServerResult.LobbyState(
                rooms = Room.Lobby.rooms.values.map { room ->
                    RoomData(
                        title = room.title,
                        roomId = room.id,
                        playerCount = room.players.count(),
                        maxCount = room.maxPlayers,
                        gameState = room.state
                    )
                }
            )
        )

        val serverMessage = GameMessage.ServerMessage(
            serverStatus = serverStatus
        )

        connectionManager.broadcastToLobby(serverMessage, json, lobbyPlayers)
    }

    suspend fun sendWords(artist: Player, words: List<String>) {
        val gameMessage = GameMessage.ServerMessage(
            serverStatus = ServerStatus.Success(
                result = ServerResult.Words(words = words)
            )
        )
        connectionManager.sendToPlayer(artist.id, gameMessage, json)
    }

    suspend fun sendDrawing(event: PlayerEvent.Drawing) {
        val room = Room.Lobby.rooms[event.player.roomId] ?: return

        val gameMessage = GameMessage.ServerMessage(
            serverStatus = ServerStatus.Success(
                result = ServerResult.DrawingState(
                    playerId = event.player.id,
                    pathData = event.pathData
                )
            )
        )

        room.players.values.forEach { player ->
            if (player.id != event.player.id) {
                connectionManager.sendToPlayer(player.id, gameMessage, json)
            }
        }
    }
}