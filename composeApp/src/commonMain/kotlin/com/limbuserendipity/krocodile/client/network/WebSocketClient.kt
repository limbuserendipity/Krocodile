package com.limbuserendipity.krocodile.client.network

import com.limbuserendipity.krocodile.client.state.ConnectionStatus
import com.limbuserendipity.krocodile.client.state.StateManager
import com.limbuserendipity.krocodile.model.GameMessage
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WebSocketClient(
    private val client: HttpClient,
    private val json: Json,
    private val messageHandler: MessageHandler,
    private val stateManager: StateManager
) {
    private var session: DefaultClientWebSocketSession? = null

    suspend fun connect(): Result<Unit> {
        return try {
            stateManager.updateConnectionStatus(ConnectionStatus.Connecting)

            client.webSocket(
                method = HttpMethod.Get,
                host = "127.0.0.1",
                port = 8080,
                path = "/game",
            ) {
                session = this
                messageHandler.setSession(this)
                stateManager.updateConnectionStatus(ConnectionStatus.Connected)
                listenForMessages()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            stateManager.updateConnectionStatus(ConnectionStatus.Error)
            Result.failure(e)
        }
    }

    private suspend fun listenForMessages() {
        try {
            session?.incoming?.consumeEach { frame ->
                when (frame) {
                    is Frame.Text -> {
                        val text = frame.readText()
                        try {
                            val message = json.decodeFromString<GameMessage>(text)
                            messageHandler.handleMessage(message)
                        } catch (e: Exception) {
                            stateManager.updateConnectionStatus(ConnectionStatus.Error)
                        }
                    }

                    is Frame.Close -> {
                        disconnect()
                    }

                    else -> Unit
                }
            }
        } catch (e: Exception) {
            disconnect()
        }
    }

    suspend fun disconnect() {
        session?.close()
        session = null
        stateManager.handleServerDisconnect()
    }

    suspend fun sendMessage(message: GameMessage): Result<Unit> {
        return try {
            session?.send(Frame.Text(json.encodeToString(message)))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}