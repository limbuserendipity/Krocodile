import com.limbuserendipity.krocodile.model.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.Json

class WebSocketHandler(
    private val playerService: PlayerService,
    private val roomService: RoomService,
    private val gameService: GameService,
    private val connectionManager: ConnectionManager,
    private val messageSender: MessageSender,
    private val json: Json
) {

    suspend fun handleMessage(session: DefaultWebSocketServerSession, text: String) {
        val message = json.decodeFromString<GameMessage>(text)

        when (message) {
            is GameMessage.PlayerMessage -> {
                handlePlayerMessage(session, message.playerEvent)
            }

            is GameMessage.ServerMessage -> {}
        }
    }

    private suspend fun handlePlayerMessage(
        session: DefaultWebSocketServerSession,
        event: PlayerEvent
    ) {
        when (event) {
            is PlayerEvent.NewPlayer -> {
                val player = playerService.registerPlayer(event.name)
                connectionManager.addSession(player.id, session)
                messageSender.sendPlayerState(player)
            }

            is PlayerEvent.NewRoom -> {
                val player = playerService.getPlayerById(event.player.id) ?: return
                val room = roomService.createRoom(player, event.title, event.maxPlayers)
                player.roomId = room.id
                Room.Lobby.rooms[room.id] = room
                Room.Lobby.players.remove(player.id)
                messageSender.sendRoomState(room)
                messageSender.sendPlayerState(player)
            }

            is PlayerEvent.LeaveRoom -> {
                val player = playerService.getPlayerById(event.player.id) ?: return
                val room = roomService.leaveRoom(player)

                if (room != null) {
                    messageSender.sendRoomState(room)
                }

                player.roomId = Room.Lobby.id
                Room.Lobby.players[player.id] = player
                messageSender.sendPlayerState(player)
                messageSender.sendLobbyState(Room.Lobby.players)
            }

            is PlayerEvent.EnterToRoom -> {
                val player = playerService.getPlayerById(event.player.id) ?: return
                val room = roomService.joinRoom(player, event.roomId) ?: return

                Room.Lobby.players.remove(player.id)

                messageSender.sendRoomState(room)
                messageSender.sendPlayerState(player)
            }

            is PlayerEvent.StartGame -> {
                val room = roomService.findRoomById(event.player.roomId) ?: return
                val artist = gameService.startRound(room)

                messageSender.sendPlayerStateToRoom(room)
                messageSender.sendWords(artist, listOf("Player, Server, Shared"))
                messageSender.sendRoomState(room)
            }

            is PlayerEvent.Word -> {
                val room = Room.Lobby.rooms[event.player.roomId] ?: return
                room.word = event.word
                room.state = GameState.Run
                messageSender.sendRoomState(room)
            }

            is PlayerEvent.ChatMessage -> {
                val room = Room.Lobby.rooms[event.player.roomId] ?: return
                room.chat.add(
                    ChatMessageData(
                        playerName = event.player.name,
                        message = event.message
                    )
                )

                if (gameService.checkGuess(room, event.message)) {
                    gameService.resetGame(room)
                }

                messageSender.sendRoomState(room)
            }

            is PlayerEvent.Drawing -> {
                messageSender.sendDrawing(event)
            }
        }

        messageSender.sendLobbyState(Room.Lobby.players)
    }
}