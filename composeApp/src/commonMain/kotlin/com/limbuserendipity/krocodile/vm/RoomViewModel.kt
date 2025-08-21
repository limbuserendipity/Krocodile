package com.limbuserendipity.krocodile.vm

import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.copy
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.limbuserendipity.krocodile.game.GameClient
import com.limbuserendipity.krocodile.model.ChatMessageData
import com.limbuserendipity.krocodile.model.PathData
import com.limbuserendipity.krocodile.model.DrawState
import com.limbuserendipity.krocodile.screen.RoomScreenState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.apply

class RoomViewModel(
    val client: GameClient
) : ViewModel() {

    val roomState = client.roomState
    val ws = client.words
    private val _screenState: MutableStateFlow<RoomScreenState> = MutableStateFlow(RoomScreenState.Loading)
    val screenState = _screenState.asStateFlow()

    private val _chatMessage = MutableSharedFlow<List<ChatMessageData>>()
    val chatMessage = _chatMessage.asSharedFlow()

    val currentPath : MutableStateFlow<Path> = MutableStateFlow(Path())
    val completedPaths : MutableStateFlow<List<Path>> = MutableStateFlow(listOf())

    val userPaths : MutableStateFlow<Map<String, Path>> = MutableStateFlow(mapOf())

    init {
        observeWords()
        observeRoomState()
        observePathData()
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

    fun observePathData() {
        viewModelScope.launch {
            client.drawingState.collect { drawing ->
                when(drawing.pathData.drawState){
                    DrawState.DrawStart -> {
                        val newPath = Path().apply {
                            moveTo(drawing.pathData.x, drawing.pathData.y)
                        }
                        userPaths.value = userPaths.value.toMutableMap().apply {
                            this[drawing.playerId] = newPath
                        }
                    }
                    DrawState.Drawing -> {
                        val existingPath = userPaths.value[drawing.playerId]
                        if (existingPath != null) {
                            val updatedPath = existingPath.copy().apply {
                                lineTo(drawing.pathData.x, drawing.pathData.y)
                            }
                            userPaths.value = userPaths.value.toMutableMap().apply {
                                this[drawing.playerId] = updatedPath
                            }
                        }
                    }
                    DrawState.DrawEnd -> {
                        val finishedPath = userPaths.value[drawing.playerId]
                        if (finishedPath != null) {
                            completedPaths.value = completedPaths.value.toMutableList().apply {
                                add(finishedPath)
                            }
                        }
                        userPaths.value = userPaths.value.toMutableMap().apply {
                            remove(drawing.playerId)
                        }
                    }
                }
            }
        }
    }

    fun Path.fromPathData(
        data : PathData,
        onEndPath : () -> Unit = {}
    ) : Path{
        when(data.drawState){
            DrawState.DrawStart -> {
                this.moveTo(data.x,  data.y)
            }
            DrawState.Drawing -> {
                this.lineTo(data.x, data.y)
            }
            DrawState.DrawEnd -> onEndPath()
        }
        return this
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

    fun onDragStart(offset : Offset){
        currentPath.value = currentPath.value.copy().apply {
            moveTo(offset.x, offset.y)
        }
        viewModelScope.launch {
            client.sendDrawingMessage(
                data = PathData(
                    x = offset.x,
                    y = offset.y,
                    drawState = DrawState.DrawStart
                )
            )
        }
    }
    fun onDrag(change : PointerInputChange, offset: Offset){
        currentPath.value = currentPath.value.copy().apply {
            lineTo(change.position.x, change.position.y)
        }
        viewModelScope.launch {
            client.sendDrawingMessage(
                data = PathData(
                    x = change.position.x,
                    y = change.position.y,
                    drawState = DrawState.Drawing
                )
            )
        }
    }

    fun onDragEnd() {
        completedPaths.value = completedPaths.value.toMutableStateList().apply {
            add(currentPath.value)
        }
        currentPath.value = Path()
        viewModelScope.launch {
            client.sendDrawingMessage(
                data = PathData(
                    x = 0f,
                    y = 0f,
                    drawState = DrawState.DrawEnd
                )
            )
        }
    }

}

