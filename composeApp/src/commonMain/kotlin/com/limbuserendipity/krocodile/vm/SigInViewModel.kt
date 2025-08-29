package com.limbuserendipity.krocodile.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.limbuserendipity.krocodile.client.GameClient
import com.limbuserendipity.krocodile.screen.UiEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


class SigInViewModel(
    val client: GameClient
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()


    init {
        connectToServer()
        observeClientState()
    }

    private fun observeClientState() {
        viewModelScope.launch {
            client.state.collect { state ->
                if (state.player != null) {
                    _uiEvent.emit(UiEvent.NavigateToLobby)
                }
            }
        }
    }

    fun connectToServer() {
        viewModelScope.launch {
            client.connect()
        }
    }

    fun sign(username: String) {
        viewModelScope.launch {
            client.createPlayer(username)
        }
    }

}