package com.limbuserendipity.krocodile.client

import com.limbuserendipity.krocodile.client.network.MessageHandler
import com.limbuserendipity.krocodile.client.network.WebSocketClient
import com.limbuserendipity.krocodile.client.service.MessageService
import com.limbuserendipity.krocodile.client.state.ClientState
import com.limbuserendipity.krocodile.client.state.ConnectionStatus
import com.limbuserendipity.krocodile.client.state.StateManager
import com.limbuserendipity.krocodile.model.ChatMessageData
import com.limbuserendipity.krocodile.model.DrawingEvent
import com.limbuserendipity.krocodile.model.GameRoomSettings
import com.limbuserendipity.krocodile.model.NotificationMessage
import io.ktor.client.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json

class GameClient(
    private val client: HttpClient,
    private val json: Json
) {
    private val stateManager = StateManager()
    private val messageHandler = MessageHandler(stateManager, json)
    private val webSocketClient = WebSocketClient(client, json, messageHandler, stateManager)
    private val messageService = MessageService(webSocketClient, stateManager)

    // Public API
    val state: StateFlow<ClientState> = stateManager.state
    val connectionStatus: StateFlow<ConnectionStatus> = stateManager.state.map { it.connectionStatus }.stateIn(
        scope = CoroutineScope(Dispatchers.Main),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ConnectionStatus.Disconnected
    )

    val notification: StateFlow<NotificationMessage?> = stateManager.state.map { it.notification }.stateIn(
        scope = CoroutineScope(Dispatchers.Main),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    suspend fun connect(): Result<Unit> {
        return webSocketClient.connect()
    }

    suspend fun disconnect() {
        webSocketClient.disconnect()
    }

    // Player actions
    suspend fun createPlayer(username: String): Result<Unit> {
        return messageService.sendNewPlayerMessage(username)
    }

    suspend fun createRoom(title: String, settings: GameRoomSettings): Result<Unit> {
        return messageService.sendCreateRoomMessage(title, settings)
    }

    suspend fun joinRoom(roomId: Long): Result<Unit> {
        return messageService.sendEnterRoomMessage(roomId)
    }

    suspend fun startGame(): Result<Unit> {
        return messageService.sendStartGameMessage()
    }

    suspend fun selectWord(word: String): Result<Unit> {
        return messageService.sendWordMessage(word)
    }

    suspend fun sendMessage(message: String): Result<Unit> {
        return messageService.sendChatMessage(message)
    }

    suspend fun sendDrawing(drawingEvent: DrawingEvent): Result<Unit> {
        return messageService.sendDrawingMessage(drawingEvent)
    }


    suspend fun setOwner(playerId: String): Result<Unit> {
        return messageService.sendSetOwner(playerId)
    }

    suspend fun kickPlayer(playerId: String): Result<Unit> {
        return messageService.sendKickPlayer(playerId)
    }

    suspend fun changeSettingsRoom(title: String, settings: GameRoomSettings): Result<Unit> {
        return messageService.sendChangeSettingsRoom(title, settings)
    }

    suspend fun fireMessage(messageData: ChatMessageData): Result<Unit> {
        return messageService.sendFireMessage(messageData)
    }

    suspend fun completeNotification(){
        stateManager.updateNotification(null)
    }

}