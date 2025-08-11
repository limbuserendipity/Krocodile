package com.limbuserendipity.krocodile.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.limbuserendipity.krocodile.game.GameClient
import com.limbuserendipity.krocodile.screen.CreateRoomState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateRoomViewModel(
    val client: GameClient
) : ViewModel() {

    val playerState = client.playerState
    val roomState = client.roomState
    private val _createRoomState : MutableStateFlow<CreateRoomState> = MutableStateFlow(CreateRoomState.Loading)
    val createRoomState : StateFlow<CreateRoomState> = _createRoomState.asStateFlow()

    init{
        observeRoomState()
    }

    private fun observeRoomState() {
        viewModelScope.launch {
            client.roomState.collect { state ->
                if(state != null && playerState.value != null){
                    if(playerState.value!!.player.id == state.owner.id){
                        _createRoomState.emit(CreateRoomState.Success)
                    }
                }
            }
        }
    }

    fun createRoom(
        title : String,
        maxPlayers : Int
    ){
        viewModelScope.launch {
            client.sendCreateRoomMessage(
                title = title,
                maxPlayers = maxPlayers
            )
        }
    }



}