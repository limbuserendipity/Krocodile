package com.limbuserendipity.krocodile.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.limbuserendipity.krocodile.client.GameClient
import com.limbuserendipity.krocodile.screen.LobbyUiEvent
import com.limbuserendipity.krocodile.screen.LobbyUiState
import com.limbuserendipity.krocodile.screen.UiEvent
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LobbyViewModel(
    val client: GameClient
) : ViewModel() {


    private val _uiState = MutableStateFlow<LobbyUiState>(LobbyUiState())
    val uiState = _uiState.asStateFlow()
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

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
        maxPlayers: Int
    ) {
        viewModelScope.launch {
            client.createRoom(title, maxPlayers)
        }
    }

    fun enterToRoom(roomId: Long) {
        viewModelScope.launch {
            client.joinRoom(roomId)
        }
    }


}