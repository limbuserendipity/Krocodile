package com.limbuserendipity.krocodile.vm

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.limbuserendipity.krocodile.game.GameClient
import com.limbuserendipity.krocodile.model.Player
import com.limbuserendipity.krocodile.screen.SigInState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class SigInViewModel(
    val client : GameClient
) : ViewModel(){

    private val _signState = MutableStateFlow<SigInState>(SigInState.Loading)
    val signState: StateFlow<SigInState> = _signState

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
                    _signState.emit(SigInState.Success)
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
                _signState.value = SigInState.Loading
                client.sendNewPlayerMessage(username)
            } catch (e: Exception) {
                _signState.value = SigInState.Failed(e.message ?: "Sign in failed")
            }
        }
    }

}