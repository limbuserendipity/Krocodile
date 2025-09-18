package com.limbuserendipity.krocodile.vm

import androidx.lifecycle.viewModelScope
import com.limbuserendipity.krocodile.client.GameClient
import com.limbuserendipity.krocodile.client.state.ConnectionStatus
import com.limbuserendipity.krocodile.screen.SigInUiEvent
import com.limbuserendipity.krocodile.screen.SigInUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class SigInViewModel(
    client: GameClient
) : BaseViewModel(client) {

    private val _uiState = MutableStateFlow<SigInUiState>(SigInUiState())
    val uiState = _uiState.asStateFlow()


    init {
        observeClientState()
    }

    private fun observeClientState() {
        viewModelScope.launch {
            client.state.collect { state ->
                if (state.player != null) {
                    _uiEvent.emit(SigInUiEvent.NavigateToLobby)
                }
            }
        }
    }

    override fun observeConnectionStatus() {
        viewModelScope.launch {
            client.connectionStatus.collect { status ->
                if (status == ConnectionStatus.Connected) {
                    if (uiState.value.name.isNotEmpty()) {
                        client.createPlayer(uiState.value.name)
                        _uiState.value = SigInUiState()
                    }
                }
            }
        }
        super.observeConnectionStatus()
    }

    fun ipAddressUpdate(ip: String) {
        _uiState.value = _uiState.value.copy(
            ip = ip
        )
    }

    fun usernameUpdate(name: String) {
        _uiState.value = _uiState.value.copy(
            name = name
        )
    }

    fun connectToServer() {
        viewModelScope.launch {
            client.connect()
        }
    }

    fun sign() {
        connectToServer()
    }

}