import com.limbuserendipity.krocodile.generateUniqueRandom
import com.limbuserendipity.krocodile.model.GameState
import com.limbuserendipity.krocodile.model.Player
import com.limbuserendipity.krocodile.model.Room
import java.util.concurrent.ConcurrentHashMap

class RoomService {
    private val rooms = ConcurrentHashMap<Long, Room.GameRoom>()

    fun createRoom(owner: Player, title: String, maxPlayers: Int): Room.GameRoom {
        val room = Room.GameRoom(
            id = generateUniqueRandom(),
            title = title,
            players = ConcurrentHashMap(),
            maxPlayers = maxPlayers,
            owner = owner,
            artist = owner,
            state = GameState.Wait,
            word = "",
            chat = mutableListOf(),
            round = 0
        )
        room.players.put(owner.id, owner)
        rooms[room.id] = room
        return room
    }

    fun findRoomById(id: Long): Room.GameRoom? = rooms[id]

    fun removeRoom(roomId: Long) {
        rooms.remove(roomId)
    }

    fun joinRoom(player: Player, roomId: Long): Room.GameRoom? {
        val room = rooms[roomId] ?: return null
        player.roomId = roomId
        room.players[player.id] = player
        return room
    }

    fun leaveRoom(player: Player): Room.GameRoom? {
        val room = rooms[player.roomId] ?: return null

        room.players.remove(player.id)

        if (room.players.isEmpty()) {
            removeRoom(room.id)
            return null
        }

        if (room.artist.id == player.id) {
            val newArtist = room.players.values.random()
            newArtist.isArtist = true
            room.artist = newArtist
            room.state = GameState.Wait
        }

        if (room.owner.id == player.id) {
            val newOwner = room.players.values.random()
            room.owner = newOwner
        }

        return room
    }

    fun kickPlayer(owner: Player, kickedPlayer: Player): Room.GameRoom? {
        val room = rooms[owner.roomId] ?: return null
        if (room.owner.id == owner.id && room.players.keys.contains(kickedPlayer.id)) {
            room.players.remove(kickedPlayer.id)
        }
        return room
    }

    fun setOwner(oldOwner: Player, newOwner: Player): Room.GameRoom? {
        val room = rooms[oldOwner.roomId] ?: return null
        if (oldOwner.id == room.owner.id) {
            if (room.players.keys.contains(newOwner.id)) {
                room.owner = newOwner
                return room
            }
        }
        return null
    }

    fun changeSettings(player: Player, title: String, maxPlayers: Int): Room.GameRoom? {
        val room = rooms[player.roomId] ?: return null
        if(player.id == room.owner.id){
            room.title = title
            room.maxPlayers = maxPlayers
        }
        return room
    }

}