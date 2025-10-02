import com.limbuserendipity.krocodile.json.gameJsonModule
import io.ktor.server.websocket.*
import kotlinx.serialization.json.Json

object GameServer {
    private val json = Json {
        serializersModule = gameJsonModule
        classDiscriminator = "type"
    }

    private val connectionManager = ConnectionManager()
    private val messageSender = MessageSender(connectionManager, json)
    private val playerService = PlayerService()
    private val roomService = RoomService()
    private val gameService = GameService(messageSender)
    private val webSocketHandler = WebSocketHandler(
        playerService,
        roomService,
        gameService,
        connectionManager,
        messageSender,
        json
    )

    suspend fun handleMessage(session: DefaultWebSocketServerSession, text: String) {
        webSocketHandler.handleMessage(session, text)
    }
}