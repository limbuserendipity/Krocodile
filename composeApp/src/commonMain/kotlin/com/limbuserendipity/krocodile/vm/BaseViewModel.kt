package com.limbuserendipity.krocodile.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.limbuserendipity.krocodile.client.GameClient
import com.limbuserendipity.krocodile.client.state.ConnectionStatus
import com.limbuserendipity.krocodile.model.NotificationMessage
import com.limbuserendipity.krocodile.model.NotificationType
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
        observeNotification()
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
                        _uiEvent.emit(
                            UiEvent.ShowMessage(
                                message = NotificationMessage(
                                    message = "Connection Error",
                                    type = NotificationType.ERROR
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    private fun observeNotification() {
        viewModelScope.launch {
            client.notification.collect { message ->
                if (message != null) {
                    _uiEvent.emit(UiEvent.ShowMessage(message))
                    client.completeNotification()
                }
            }
        }
    }

}