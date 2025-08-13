package com.limbuserendipity.krocodile.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.limbuserendipity.krocodile.game.GameClient
import com.limbuserendipity.krocodile.model.ChatMessageData
import com.limbuserendipity.krocodile.screen.RoomScreenState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RoomViewModel(
    val client: GameClient
) : ViewModel() {

    val roomState = client.roomState
    val ws = client.words
    private val _screenState: MutableStateFlow<RoomScreenState> = MutableStateFlow(RoomScreenState.Loading)
    val screenState = _screenState.asStateFlow()

    private val _chatMessage = MutableSharedFlow<List<ChatMessageData>>()
    val chatMessage = _chatMessage.asSharedFlow()


    init {
        observeWords()
        observeRoomState()
    }

    fun observeWords() {
        viewModelScope.launch {
            client.words.collect { words ->
                println("observeWords")
                _screenState.emit(RoomScreenState.Success(words))
            }
        }
    }

    fun observeRoomState(){
        viewModelScope.launch {
            client.roomState.collect { roomState ->
                roomState?.let {
                    _chatMessage.emit(it.chat)
                }
            }
        }
    }

    fun sendWordMessage(word: String) {
        viewModelScope.launch {
            client.sendWordMessage(word)
            _screenState.emit(RoomScreenState.Loading)
        }
    }

    fun sendChatMessage(message : String){
        viewModelScope.launch {
            client.sendChatMessage(message)
        }
    }

}

