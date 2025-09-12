package com.limbuserendipity.krocodile.client.state

import com.limbuserendipity.krocodile.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class StateManager {
    private val _state = MutableStateFlow(ClientState())
    val state: StateFlow<ClientState> = _state.asStateFlow()

    private fun updateState(newState: ClientState) {
        println("artist = ${newState.artist}")
        println("availableWords = ${newState.availableWords}")
        println("drawingEvent = ${newState.drawingEvent}")
        println("player = ${newState.player}")
        println("round = ${newState.round}")
        println("chatMessages = ${newState.chatMessages}")
        println("owner = ${newState.owner}")
        println("currentRoom = ${newState.currentRoom}")
        println("lobbyRooms = ${newState.lobbyRooms}")
        println("isArtist = ${newState.isArtist}")
        _state.value = newState
    }

    fun updatePlayer(player: Player) {
        updateState(_state.value.copy(player = player))
    }

    fun updateLobbyState(rooms: List<RoomData>) {
        updateState(_state.value.copy(lobbyRooms = rooms))
    }

    fun updateRoomState(
        roomData: RoomData,
        players: List<PlayerData>,
        chat: List<ChatMessageData>,
        isArtist: Boolean,
        owner: PlayerData,
        artist: PlayerData,
        round: Int
    ) {
        updateState(
            _state.value.copy(
                currentRoom = roomData,
                roomPlayers = players,
                chatMessages = chat,
                isArtist = isArtist,
                owner = owner,
                artist = artist,
                round = round
            )
        )
    }

    fun updateWords(words: List<String>) {
        updateState(_state.value.copy(availableWords = words))
    }

    fun updateConnectionStatus(status: ConnectionStatus) {
        updateState(
            _state.value.copy(connectionStatus = status)
        )
    }

    fun updateDrawingEvent(drawingEvent: DrawingEvent) {
        updateState(
            _state.value.copy(drawingEvent = drawingEvent)
        )
    }

    fun addChatMessage(message: ChatMessageData) {
        val currentChat = _state.value.chatMessages.toMutableList()
        currentChat.add(message)
        _state.value = _state.value.copy(chatMessages = currentChat)
    }
}