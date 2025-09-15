import com.limbuserendipity.krocodile.model.GameState
import com.limbuserendipity.krocodile.model.Player
import com.limbuserendipity.krocodile.model.Room

class GameService {

    var iterator = 0

    fun startRound(room: Room.GameRoom): Player {
        if (iterator >= room.players.count()) {
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

    fun resetGame(winner: Player, room: Room.GameRoom) {
        room.state = GameState.End(
            winnerName = winner.name,
            guessedWord = room.word
        )
        room.word = ""
        room.chat.clear()
        room.players[winner.id].apply {
            this?.score = score + 11
        }
    }
}