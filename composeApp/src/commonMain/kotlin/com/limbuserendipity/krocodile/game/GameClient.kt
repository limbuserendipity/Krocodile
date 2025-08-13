package com.limbuserendipity.krocodile.game

import com.limbuserendipity.krocodile.model.GameMessage
import com.limbuserendipity.krocodile.model.PlayerEvent
import com.limbuserendipity.krocodile.model.ServerResult
import com.limbuserendipity.krocodile.model.ServerStatus
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocket

import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class GameClient(
    private val client: HttpClient,
    private val json: Json
) {
    private var session: DefaultClientWebSocketSession? = null

    private val _serverMessages = MutableSharedFlow<GameMessage>()
    val serverMessages: SharedFlow<GameMessage> = _serverMessages

    private val _playerState = MutableStateFlow<ServerResult.PlayerState?>(null)
    val playerState: StateFlow<ServerResult.PlayerState?> = _playerState.asStateFlow()

    private val _lobbyState = MutableStateFlow<ServerResult.LobbyState?>(null)
    val lobbyState: StateFlow<ServerResult.LobbyState?> = _lobbyState.asStateFlow()


    private val _roomState = MutableStateFlow<ServerResult.RoomState?>(null)
    val roomState: StateFlow<ServerResult.RoomState?> = _roomState.asStateFlow()

    private val _words = MutableSharedFlow<List<String>>()
    val words = _words.asSharedFlow()

    suspend fun connect() {
        delay(2000)
        client.webSocket(
            method = HttpMethod.Get,
            host = "127.0.0.1",
            port = 8080,
            path = "/game",
        ) {
            session = this
            launchMessageListener()
        }

    }

    private suspend fun DefaultClientWebSocketSession.launchMessageListener() {
        incoming.consumeEach { frame ->
            when (frame) {
                is Frame.Text -> {
                    val text = frame.readText()
                    println(text)
                    val message = json.decodeFromString<GameMessage>(text)
                    handleServerMessage(message as GameMessage.ServerMessage)
                }

                else -> Unit
            }
        }
    }

    suspend fun sendNewPlayerMessage(username: String) {
        val gameMessage = GameMessage.PlayerMessage(playerEvent = PlayerEvent.NewPlayer(username))
        sendMessage(gameMessage)
    }

    suspend fun sendCreateRoomMessage(
        title: String,
        maxPlayers: Int
    ) {
        val gameMessage = GameMessage.PlayerMessage(
            playerEvent = PlayerEvent.NewRoom(
                player = playerState.value!!.player,
                title = title,
                maxPlayers = maxPlayers
            )
        )
        sendMessage(gameMessage)
    }

    suspend fun sendEnterToRoomMessage(
        roomId : Long
    ){
        val gameMessage = GameMessage.PlayerMessage(
            playerEvent = PlayerEvent.EnterToRoom(player = playerState.value!!.player, roomId = roomId)
        )
        sendMessage(gameMessage)
    }

    private suspend fun sendMessage(message: GameMessage) {
        println("sendMessage")
        session?.send(Frame.Text(json.encodeToString(message)))
    }

    suspend fun handleServerMessage(message: GameMessage.ServerMessage) {
        println("handleServerMessage")
        when (message.serverStatus) {
            is ServerStatus.Success -> {
                val result = (message.serverStatus as ServerStatus.Success).result

                when (result) {
                    is ServerResult.LobbyState -> {
                        println("LobbyState")
                        _lobbyState.emit(result)
                    }

                    is ServerResult.PlayerState -> {
                        println("PlayerState")
                        _playerState.emit(result)
                    }

                    is ServerResult.RoomState -> {
                        println("RoomState")
                        _roomState.emit(result)
                    }
                    is ServerResult.Words -> {
                        println("Words")
                        _words.emit(result.words)
                    }
                }
            }

            is ServerStatus.Error -> TODO()
        }
    }


    suspend fun disconnect() {
        session?.close()
        session = null
    }

    suspend fun sendWordMessage(word: String) {
        val gameMessage = GameMessage.PlayerMessage(
            playerEvent = PlayerEvent.Word(
                player = playerState.value!!.player,
                word = word
            )
        )
        sendMessage(gameMessage)
    }

}