import com.limbuserendipity.krocodile.model.GameMessage
import com.limbuserendipity.krocodile.model.Player
import com.limbuserendipity.krocodile.model.Room
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class ConnectionManager {
    private val connections = ConcurrentHashMap<String, DefaultWebSocketServerSession>()

    fun addSession(playerId: String, session: DefaultWebSocketServerSession) {
        connections[playerId] = session
    }

    fun removeSession(playerId: String) {
        connections.remove(playerId)
    }

    fun getSession(playerId: String): DefaultWebSocketServerSession? = connections[playerId]

    suspend fun sendToPlayer(playerId: String, message: GameMessage, json: Json) {
        connections[playerId]?.let { session ->
            session.send(Frame.Text(json.encodeToString(message)))
        }
    }

    suspend fun broadcastToRoom(room: Room.GameRoom, message: GameMessage, json: Json) {
        val messageText = json.encodeToString(message)
        room.players.values.forEach { player ->
            connections[player.id]?.send(Frame.Text(messageText))
        }
    }

    suspend fun broadcastToLobby(message: GameMessage, json: Json, lobbyPlayers: ConcurrentHashMap<String, Player>) {
        val messageText = json.encodeToString(message)
        lobbyPlayers.keys.forEach { playerId ->
            connections[playerId]?.send(Frame.Text(messageText))
        }
    }
}