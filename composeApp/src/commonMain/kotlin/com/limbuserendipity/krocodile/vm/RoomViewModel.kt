package com.limbuserendipity.krocodile.vm

import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.copy
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.limbuserendipity.krocodile.client.GameClient
import com.limbuserendipity.krocodile.model.DrawState
import com.limbuserendipity.krocodile.model.PathData
import com.limbuserendipity.krocodile.screen.RoomUiState
import com.limbuserendipity.krocodile.screen.UiEvent
import com.limbuserendipity.krocodile.screen.roomUiStatePlaceHolder
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RoomViewModel(
    val client: GameClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(roomUiStatePlaceHolder())
    val uiState = _uiState.asStateFlow()
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    val currentPath: MutableStateFlow<Path> = MutableStateFlow(Path())
    val completedPaths: MutableStateFlow<List<Path>> = MutableStateFlow(listOf())

    val userPaths: MutableStateFlow<Map<String, Path>> = MutableStateFlow(mapOf())

    init {
        observeClientState()

        observePathData()
    }

    fun observeClientState() {
        viewModelScope.launch {
            client.state.collect { state ->
                _uiState.emit(
                    RoomUiState(
                        roomData = state.currentRoom!!,
                        players = state.roomPlayers,
                        owner = state.owner!!,
                        artist = state.artist!!,
                        chat = state.chatMessages,
                        availableWords = state.availableWords,
                        round = state.round
                    )
                )
            }
        }
    }

    fun observePathData() {
        viewModelScope.launch {
            client.state.collect { state ->
                when (state.pathData?.drawState) {
                    DrawState.DrawStart -> {
                        val newPath = Path().apply {
                            moveTo(state.pathData.x, state.pathData.y)
                        }
                        userPaths.value = userPaths.value.toMutableMap().apply {
                            this[state.pathData.drawerId] = newPath
                        }
                    }

                    DrawState.Drawing -> {
                        val existingPath = userPaths.value[state.pathData.drawerId]
                        if (existingPath != null) {
                            val updatedPath = existingPath.copy().apply {
                                lineTo(state.pathData.x, state.pathData.y)
                            }
                            userPaths.value = userPaths.value.toMutableMap().apply {
                                this[state.pathData.drawerId] = updatedPath
                            }
                        }
                    }

                    DrawState.DrawEnd -> {
                        val finishedPath = userPaths.value[state.pathData.drawerId]
                        if (finishedPath != null) {
                            completedPaths.value = completedPaths.value.toMutableList().apply {
                                add(finishedPath)
                            }
                        }
                        userPaths.value = userPaths.value.toMutableMap().apply {
                            remove(state.pathData.drawerId)
                        }
                    }

                    null -> {

                    }
                }
            }
        }
    }

    fun Path.fromPathData(
        data: PathData,
        onEndPath: () -> Unit = {}
    ): Path {
        when (data.drawState) {
            DrawState.DrawStart -> {
                this.moveTo(data.x, data.y)
            }

            DrawState.Drawing -> {
                this.lineTo(data.x, data.y)
            }

            DrawState.DrawEnd -> onEndPath()
        }
        return this
    }

    fun startGame() {
        viewModelScope.launch {
            client.startGame()
        }
    }

    fun sendWordMessage(word: String) {
        viewModelScope.launch {
            client.selectWord(word)
        }
    }

    fun sendChatMessage(message: String) {
        viewModelScope.launch {
            client.sendMessage(message)
        }
    }

    fun onDragStart(offset: Offset) {
        currentPath.value = currentPath.value.copy().apply {
            moveTo(offset.x, offset.y)
        }

        viewModelScope.launch {
            client.sendDrawing(offset.x, offset.y, DrawState.DrawStart, 0xFF000000)
        }
    }

    fun onDrag(change: PointerInputChange, offset: Offset) {
        currentPath.value = currentPath.value.copy().apply {
            lineTo(change.position.x, change.position.y)
        }
        viewModelScope.launch {
            client.sendDrawing(change.position.x, change.position.y, DrawState.Drawing, 0xFF000000)
        }
    }

    fun onDragEnd() {
        completedPaths.value = completedPaths.value.toMutableStateList().apply {
            add(currentPath.value)
        }
        currentPath.value = Path()
        viewModelScope.launch {
            client.sendDrawing(0f, 0f, DrawState.DrawEnd, 0xFF000000)
        }
    }

}

