import com.limbuserendipity.krocodile.model.GameState
import com.limbuserendipity.krocodile.model.Player
import com.limbuserendipity.krocodile.model.Room

class GameService {

    private val words = listOf("Tree", "Home", "Car")
    var iterator = 0

    fun startRound(room: Room.GameRoom) : Player{
        if(iterator >= room.players.count()){
            iterator = 0
        }
        room.state = GameState.Starting
        val previousArtist = room.artist
        previousArtist.isArtist = false
        val artist = (room.players.values.toList())[iterator]
        artist.isArtist = true
        room.artist = artist
        iterator++
        room.round++
        return artist
    }

    fun checkGuess(room: Room.GameRoom, guess: String): Boolean {
        return guess.lowercase() == room.word.lowercase()
    }

    fun resetGame(room: Room.GameRoom) {
        room.word = ""
        room.state = GameState.Wait
        room.chat.clear()

    }
}