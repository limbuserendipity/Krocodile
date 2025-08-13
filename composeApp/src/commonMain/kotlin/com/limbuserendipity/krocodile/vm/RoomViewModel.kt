package com.limbuserendipity.krocodile.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.limbuserendipity.krocodile.game.GameClient
import com.limbuserendipity.krocodile.screen.RoomScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RoomViewModel(
    val client : GameClient
) : ViewModel() {

    val roomState = client.roomState
    val ws = client.words
    private val _screenState : MutableStateFlow<RoomScreenState> = MutableStateFlow(RoomScreenState.Loading)
    val screenState = _screenState.asStateFlow()

    init {
        observeWords()
    }

    fun observeWords(){
        viewModelScope.launch {
            client.words.collect { words ->
                println("observeWords")
                _screenState.emit(RoomScreenState.Success(words))
            }
        }
    }

    fun sendWordMessage(word : String){
        viewModelScope.launch {
            client.sendWordMessage(word)
        }
    }

}

