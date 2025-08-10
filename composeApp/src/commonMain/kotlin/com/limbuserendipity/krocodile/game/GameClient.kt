package com.limbuserendipity.krocodile.game

import com.limbuserendipity.krocodile.model.GameMessage
import com.limbuserendipity.krocodile.model.Player
import com.limbuserendipity.krocodile.model.PlayerEvent
import com.limbuserendipity.krocodile.model.ServerResult
import com.limbuserendipity.krocodile.model.ServerStatus
import io.github.aakira.napier.Napier
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
    val lobbyState : StateFlow<ServerResult.LobbyState?> = _lobbyState.asStateFlow()

    init {

    }
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
    suspend fun sendMessage(message: GameMessage) {
        println("sendMessage")
        session?.send(Frame.Text(json.encodeToString(message)))
    }

    suspend fun handleServerMessage(message: GameMessage.ServerMessage) {
        println("handleServerMessage")
        when (message.serverStatus) {
            is ServerStatus.Success -> {
                val result = (message.serverStatus as ServerStatus.Success).result

                when(result){
                    is ServerResult.LobbyState -> {
                        println("LobbyState")
                        _lobbyState.emit(result)
                    }
                    is ServerResult.PlayerState -> {
                        println("PlayerState")
                        _playerState.emit(result )
                    }
                    is ServerResult.RoomState -> TODO()
                }

            }

            is ServerStatus.Error -> TODO()
        }
    }


    suspend fun disconnect() {
        session?.close()
        session = null
    }

}