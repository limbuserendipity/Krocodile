import com.limbuserendipity.krocodile.model.EndVariant
import com.limbuserendipity.krocodile.model.GameState
import com.limbuserendipity.krocodile.model.Player
import com.limbuserendipity.krocodile.model.Room
import com.limbuserendipity.krocodile.util.getRandomDrawingWords
import kotlinx.coroutines.*
import kotlin.coroutines.cancellation.CancellationException

class GameService(
    private val messageSender: MessageSender
) {

    private val roomsIterator = mutableMapOf<Long, Int>()
    private val roundJobs = mutableMapOf<Long, Job>()

    suspend fun startingGame(room: Room.GameRoom) {
        roundJobs[room.id]?.cancel()
        roomsIterator[room.id] = 0
        room.chat.clear()
        room.word = ""
        startRound(room)
    }

    suspend fun startRound(room: Room.GameRoom) {
        val previousArtist = resetRound(room)
        val artist = selectArtist(room)
        val words = getRandomDrawingWords()

        messageSender.sendPlayerState(previousArtist)
        messageSender.sendPlayerState(artist)
        messageSender.sendWords(room.artist, words)

        roundJobs[room.id] = CoroutineScope(Dispatchers.Default).launch {

            if (room.round < 0) room.round = 0

            if (room.round < room.settings.maxRounds) {
                room.round++
                var timer = 10
                while (timer >= 0) {
                    timer--
                    delay(1000)
                    room.state = GameState.Starting(timer)
                    messageSender.sendRoomState(room)
                }
                if (isActive) {
                    room.round--
                    startRound(room)
                }
            } else {
                gameEnd(room)
            }
        }
    }

    fun gameEnd(room: Room.GameRoom) {
        roundJobs[room.id]?.cancel()
        var timer = 10
        roundJobs[room.id] = CoroutineScope(Dispatchers.Default).launch {
            room.state = GameState.End(
                EndVariant.GameEnd(
                    winnerName = room.players.values.maxBy { it.score }.name
                )
            )
            messageSender.sendRoomState(room)

            while (timer >= 0) {
                timer--
                delay(1000)
            }

            room.state = GameState.Wait
            messageSender.sendRoomState(room)

        }
    }

    fun selectWord(room: Room.GameRoom, word: String) {
        roundJobs[room.id]?.cancel()
        room.word = word
        runRound(room)
    }

    fun runRound(room: Room.GameRoom) {
        roundJobs[room.id]?.cancel()
        startRoundTimer(room, room.settings.maxTime)
    }

    private fun startRoundTimer(room: Room.GameRoom, roundTimeSeconds: Int) {
        roundJobs[room.id] = CoroutineScope(Dispatchers.Default).launch {
            var remainingTime = roundTimeSeconds

            try {
                while (remainingTime >= 0 && isActive) {
                    room.state = GameState.Run(
                        time = remainingTime
                    )
                    messageSender.sendRoomState(room)
                    delay(1000)
                    remainingTime--
                }

                if (isActive) {
                    timeUp(room)
                }
            } catch (e: CancellationException) {
                println("Round timer cancelled for room ${room.id}")
            }
        }
    }

    private suspend fun timeUp(room: Room.GameRoom) {
        var timer = 10
        while (timer >= 0) {

            room.state = GameState.End(
                EndVariant.FailedWord(
                    guessedWord = room.word,
                    time = timer
                )
            )

            messageSender.sendRoomState(room)

            timer--
            delay(1000)
        }

        startRound(room)
    }

    private suspend fun resetRound(room: Room.GameRoom): Player {
        room.word = ""
        room.chat.clear()
        val previousArtist = room.artist
        previousArtist?.isArtist = false
        return previousArtist
    }

    suspend fun checkGuess(room: Room.GameRoom, winner: Player, guess: String) {
        val isCorrect = guess.equals(room.word, ignoreCase = true)
        if (isCorrect) {
            roundJobs[room.id]?.cancel()
            roundJobs[room.id] = CoroutineScope(Dispatchers.Default).launch {
                var timer = 10
                while (timer >= 10) {
                    room.state = GameState.End(
                        endVariant =
                            EndVariant.GuessedWord(
                                winnerName = winner.name,
                                guessedWord = room.word,
                                time = timer
                            )
                    )
                    messageSender.sendRoomState(room)
                    timer--
                    delay(1000)
                }
                room.players[winner.id]?.apply {
                    this.score = score + 11
                }

                startRound(room)

            }

        }
    }

    fun selectArtist(room: Room.GameRoom): Player {
        var iterator = roomsIterator.getOrDefault(room.id, 0)
        if (iterator >= room.players.count()) {
            iterator = 0
        }

        val artist = (room.players.values.toList())[iterator]
        artist.isArtist = true
        room.artist = artist
        roomsIterator[room.id] = iterator + 1

        return artist
    }

    fun stopRoundTimer(roomId: Long) {
        roundJobs[roomId]?.cancel()
        roundJobs.remove(roomId)
    }

    fun cleanupRoom(roomId: Long) {
        stopRoundTimer(roomId)
    }
}
