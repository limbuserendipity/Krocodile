package com.limbuserendipity.krocodile.client.service

import com.limbuserendipity.krocodile.client.network.WebSocketClient
import com.limbuserendipity.krocodile.client.state.StateManager
import com.limbuserendipity.krocodile.model.DrawingEvent
import com.limbuserendipity.krocodile.model.GameMessage
import com.limbuserendipity.krocodile.model.PlayerEvent

class MessageService(
    private val webSocketClient: WebSocketClient,
    private val stateManager: StateManager
) {

    suspend fun sendNewPlayerMessage(username: String): Result<Unit> {
        val message = GameMessage.PlayerMessage(
            playerEvent = PlayerEvent.NewPlayer(username)
        )
        return webSocketClient.sendMessage(message)
    }

    suspend fun sendCreateRoomMessage(title: String, maxPlayers: Int): Result<Unit> {
        val player = stateManager.state.value.player ?: return Result.failure(IllegalStateException("No player"))
        val message = GameMessage.PlayerMessage(
            playerEvent = PlayerEvent.NewRoom(
                player = player,
                title = title,
                maxPlayers = maxPlayers
            )
        )
        return webSocketClient.sendMessage(message)
    }

    suspend fun sendEnterRoomMessage(roomId: Long): Result<Unit> {
        val player = stateManager.state.value.player ?: return Result.failure(IllegalStateException("No player"))
        val message = GameMessage.PlayerMessage(
            playerEvent = PlayerEvent.EnterToRoom(
                player = player,
                roomId = roomId
            )
        )
        return webSocketClient.sendMessage(message)
    }

    suspend fun sendStartGameMessage(): Result<Unit> {
        val player = stateManager.state.value.player ?: return Result.failure(IllegalStateException("No player"))
        val message = GameMessage.PlayerMessage(
            playerEvent = PlayerEvent.StartGame(
                player = player
            )
        )
        return webSocketClient.sendMessage(message)
    }

    suspend fun sendWordMessage(word: String): Result<Unit> {
        val player = stateManager.state.value.player ?: return Result.failure(IllegalStateException("No player"))
        val message = GameMessage.PlayerMessage(
            playerEvent = PlayerEvent.Word(
                player = player,
                word = word
            )
        )
        return webSocketClient.sendMessage(message)
    }

    suspend fun sendChatMessage(message: String): Result<Unit> {
        val player = stateManager.state.value.player ?: return Result.failure(IllegalStateException("No player"))
        val gameMessage = GameMessage.PlayerMessage(
            playerEvent = PlayerEvent.ChatMessage(
                player = player,
                message = message
            )
        )
        return webSocketClient.sendMessage(gameMessage)
    }

    suspend fun sendDrawingMessage(drawingEvent: DrawingEvent): Result<Unit> {
        val player = stateManager.state.value.player ?: return Result.failure(IllegalStateException("No player"))
        val message = GameMessage.PlayerMessage(
            playerEvent = PlayerEvent.Drawing(
                player = player,
                drawingEvent = drawingEvent
            )
        )
        return webSocketClient.sendMessage(message)
    }

    suspend fun sendSetOwner(playerId: String): Result<Unit> {
        val player = stateManager.state.value.player ?: return Result.failure(IllegalStateException("No player"))
        val message = GameMessage.PlayerMessage(
            playerEvent = PlayerEvent.SetOwner(
                player = player,
                newOwnerId = playerId,
            )
        )
        return webSocketClient.sendMessage(message)
    }

    suspend fun sendKickPlayer(playerId: String): Result<Unit> {
        val player = stateManager.state.value.player ?: return Result.failure(IllegalStateException("No player"))
        val message = GameMessage.PlayerMessage(
            playerEvent = PlayerEvent.KickPlayer(
                player = player,
                kickedPlayerId = playerId,
            )
        )
        return webSocketClient.sendMessage(message)
    }
}