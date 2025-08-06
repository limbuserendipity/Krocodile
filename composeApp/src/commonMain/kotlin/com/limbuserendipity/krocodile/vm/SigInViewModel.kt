package com.limbuserendipity.krocodile.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.limbuserendipity.krocodile.game.GameClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class SigInViewModel(
    val client : GameClient
) : ViewModel(){

    fun connectToServer(){
        viewModelScope.launch {
            client.connect()
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