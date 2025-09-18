package com.limbuserendipity.krocodile.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.limbuserendipity.krocodile.client.GameClient
import com.limbuserendipity.krocodile.client.state.ConnectionStatus
import com.limbuserendipity.krocodile.screen.UiEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

open class BaseViewModel(
    val client: GameClient
) : ViewModel() {

    protected val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    init {
        observeConnectionStatus()
        observeErrorMessage()
    }

    open fun observeConnectionStatus() {
        viewModelScope.launch {
            client.connectionStatus.collect { status ->
                when (status) {
                    ConnectionStatus.Disconnected -> {
                        _uiEvent.emit(UiEvent.Disconnect)
                    }

                    ConnectionStatus.Connecting -> {}
                    ConnectionStatus.Connected -> {}
                    ConnectionStatus.Error -> {
                        _uiEvent.emit(UiEvent.ShowError("Error"))
                    }
                }
            }
        }
    }

    private fun observeErrorMessage() {
        viewModelScope.launch {
            client.errorMessage.collect { message ->
                _uiEvent.emit(UiEvent.ShowError(message))
            }
        }
    }
}