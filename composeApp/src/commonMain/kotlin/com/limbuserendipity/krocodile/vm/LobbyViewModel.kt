package com.limbuserendipity.krocodile.vm

import androidx.lifecycle.viewModelScope
import com.limbuserendipity.krocodile.client.GameClient
import com.limbuserendipity.krocodile.model.GameRoomSettings
import com.limbuserendipity.krocodile.screen.LobbyUiEvent
import com.limbuserendipity.krocodile.screen.LobbyUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LobbyViewModel(
    client: GameClient
) : BaseViewModel(client) {


    private val _uiState = MutableStateFlow<LobbyUiState>(LobbyUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeClientState()
    }

    private fun observeClientState() {
        viewModelScope.launch {
            client.state.collect { state ->

                if (state.currentRoom != null) {
                    _uiEvent.emit(LobbyUiEvent.NavigateToRoom)
                }

                _uiState.emit(
                    LobbyUiState(
                        rooms = state.lobbyRooms
                    )
                )
            }
        }
    }

    fun onShowCreateRoomDialog() {
        viewModelScope.launch {
            _uiEvent.emit(LobbyUiEvent.ShowCreateRoomDialog(true))
        }
    }

    fun createRoom(
        title: String,
        settings: GameRoomSettings
    ) {
        viewModelScope.launch {
            client.createRoom(title, settings)
        }
    }

    fun enterToRoom(roomId: Long) {
        viewModelScope.launch {
            client.joinRoom(roomId)
        }
    }

}