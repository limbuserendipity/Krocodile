package com.limbuserendipity.krocodile.game

import com.limbuserendipity.krocodile.model.GameMessage
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocket

import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class GameClient(
    private val client: HttpClient
) {
    private var session: DefaultClientWebSocketSession? = null

    private val _serverMessages = MutableSharedFlow<GameMessage>()
    val serverMessages: SharedFlow<GameMessage> = _serverMessages

    suspend fun connect() {
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
                    val message = Json.decodeFromString<GameMessage>(frame.readText())
                    _serverMessages.emit(message)
                }

                else -> Unit
            }
        }
    }

    suspend fun sendMessage(message: GameMessage) {
        session?.send(Frame.Text(Json.encodeToString(message)))
    }

    suspend fun disconnect() {
        session?.close()
        session = null
    }
}