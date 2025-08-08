package com.limbuserendipity.krocodile.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.limbuserendipity.krocodile.game.GameClient
import com.limbuserendipity.krocodile.model.GameMessage
import com.limbuserendipity.krocodile.model.PlayerEvent
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.logger.Logger


class SigInViewModel(
    val client : GameClient
) : ViewModel(){

    val playerState = client.playerState

    init {
        connectToServer()
    }

    fun connectToServer(){
        viewModelScope.launch {
            client.connect()
            Napier.w("connect")
        }
    }

    fun newPlayer(){
        viewModelScope.launch {
            val message = GameMessage.PlayerMessage(PlayerEvent.NewPlayer("Sus"))
            client.sendMessage(message)
        }
    }

    private val _counter = MutableStateFlow(0)
    val counter: StateFlow<Int> = _counter

    // Increment method
    fun increment() {
        _counter.value += 1
    }

    // Decrement method
    fun decrement() {
        _counter.value -= 1
    }


}