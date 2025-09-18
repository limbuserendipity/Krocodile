package com.limbuserendipity.krocodile.client.network

import com.limbuserendipity.krocodile.client.state.StateManager
import com.limbuserendipity.krocodile.model.GameMessage
import com.limbuserendipity.krocodile.model.ServerResult
import com.limbuserendipity.krocodile.model.ServerStatus
import io.ktor.client.plugins.websocket.*
import kotlinx.serialization.json.Json

class MessageHandler(
    private val stateManager: StateManager,
    private val json: Json
) {
    private var session: DefaultClientWebSocketSession? = null

    fun setSession(session: DefaultClientWebSocketSession) {
        this.session = session
    }

    suspend fun handleMessage(message: GameMessage) {
        when (message) {
            is GameMessage.ServerMessage -> handleServerMessage(message)
            is GameMessage.PlayerMessage -> Unit // Client shouldn't receive player messages
        }
    }

    private suspend fun handleServerMessage(message: GameMessage.ServerMessage) {
        when (val result = message.serverStatus) {
            is ServerStatus.Success -> {
                when (val data = result.result) {
                    is ServerResult.PlayerState -> {
                        stateManager.updatePlayer(data.player)
                    }

                    is ServerResult.LobbyState -> {
                        stateManager.updateLobbyState(data.rooms)
                    }

                    is ServerResult.RoomState -> {
                        stateManager.updateRoomState(
                            roomData = data.roomData,
                            players = data.players,
                            chat = data.chat,
                            owner = data.owner,
                            artist = data.artist,
                            round = data.round,
                            isArtist = data.artist.id == stateManager.state.value.player?.id
                        )
                    }

                    is ServerResult.Words -> {
                        stateManager.updateWords(data.words)
                    }

                    is ServerResult.DrawingState -> {
                        stateManager.updateDrawingEvent(drawingEvent = data.drawingEvent)
                    }
                }
            }

            is ServerStatus.Error -> {
                stateManager.updateErrorMessage(result.message)
            }
        }
    }
}