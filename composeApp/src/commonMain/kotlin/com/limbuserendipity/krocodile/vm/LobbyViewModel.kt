package com.limbuserendipity.krocodile.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.limbuserendipity.krocodile.game.GameClient
import com.limbuserendipity.krocodile.screen.ScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LobbyViewModel(
    val client: GameClient
) : ViewModel() {

    val lobbyState = client.lobbyState
    val roomState = client.roomState
    private val _screenState = MutableStateFlow<ScreenState>(ScreenState.Loading)
    val screenState: StateFlow<ScreenState> = _screenState

    fun observeRoomState() {
        viewModelScope.launch {
            client.roomState.collect { room ->
                if (room != null) {
                    if (client.playerState.value?.player?.roomId == room.roomData.roomId) {
                        _screenState.emit(ScreenState.Success)
                    }
                }
            }
        }
    }

    fun enterToRoom(roomId: Long) {
        viewModelScope.launch {
            client.sendEnterToRoomMessage(roomId = roomId)
        }
    }


}