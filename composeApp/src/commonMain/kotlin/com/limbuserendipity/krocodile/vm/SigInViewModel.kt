package com.limbuserendipity.krocodile.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.limbuserendipity.krocodile.game.GameClient
import com.limbuserendipity.krocodile.model.Player
import com.limbuserendipity.krocodile.screen.ScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class SigInViewModel(
    val client : GameClient
) : ViewModel(){

    private val _screenState = MutableStateFlow<ScreenState>(ScreenState.Loading)
    val screenState: StateFlow<ScreenState> = _screenState

    private val _playerState = MutableStateFlow<Player?>(null)
    val playerState: StateFlow<Player?> = _playerState

    init {
        connectToServer()
        observePlayerState()
    }

    private fun observePlayerState() {
        viewModelScope.launch {
            client.playerState.collect { state ->
                if (state != null) {
                    _playerState.emit(state.player)
                    _screenState.emit(ScreenState.Success)
                }
            }
        }
    }

    fun connectToServer(){
        viewModelScope.launch {
            client.connect()
        }
    }

    fun sign(username: String) {
        viewModelScope.launch {
            try {
                _screenState.value = ScreenState.Loading
                client.sendNewPlayerMessage(username)
            } catch (e: Exception) {
                _screenState.value = ScreenState.Failed(e.message ?: "Sign in failed")
            }
        }
    }

}