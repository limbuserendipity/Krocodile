import com.limbuserendipity.krocodile.generatePlayerId
import com.limbuserendipity.krocodile.model.Player
import com.limbuserendipity.krocodile.model.Room
import java.util.concurrent.ConcurrentHashMap

class PlayerService {
    private val players = ConcurrentHashMap<String, Player>()

    fun registerPlayer(name: String): Player {
        val player = Player(
            id = generatePlayerId(),
            name = name,
            isArtist = false,
            roomId = Room.Lobby.id,
            score = 0
        )
        players[player.id] = player
        return player
    }

    fun getPlayerById(id: String): Player? = players[id]

    fun removePlayer(playerId: String) {
        players.remove(playerId)
    }
}