package com.limbuserendipity.krocodile.vm

import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.copy
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.limbuserendipity.krocodile.client.GameClient
import com.limbuserendipity.krocodile.model.*
import com.limbuserendipity.krocodile.screen.RoomUiEvent
import com.limbuserendipity.krocodile.screen.RoomUiState
import com.limbuserendipity.krocodile.screen.UiEvent
import com.limbuserendipity.krocodile.screen.roomUiStatePlaceHolder
import com.limbuserendipity.krocodile.theme.CanvasSurface
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RoomViewModel(
    val client: GameClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(roomUiStatePlaceHolder())
    val uiState = _uiState.asStateFlow()
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    val currentPath: MutableStateFlow<PathInfo> = MutableStateFlow(pathInfoPlaceHolder())

    val completedPaths: MutableStateFlow<List<PathInfo>> = MutableStateFlow(listOf())

    val userPaths: MutableStateFlow<Map<String, PathInfo>> = MutableStateFlow(mapOf())


    init {
        observeClientState()
        observePathData()
    }

    fun observeClientState() {
        viewModelScope.launch {
            client.state.collect { state ->
                _uiState.emit(
                    RoomUiState(
                        player = state.player!!,
                        roomData = state.currentRoom!!,
                        players = state.roomPlayers,
                        owner = state.owner!!,
                        artist = state.artist!!,
                        isArtist = state.isArtist,
                        chat = state.chatMessages,
                        availableWords = state.availableWords,
                        round = state.round
                    )
                )

                if (state.player.roomId == 0L) {
                    _uiEvent.emit(RoomUiEvent.NavigateToLobby)
                }

                if (state.currentRoom.gameState == GameState.Starting && state.player.isArtist) {
                    _uiEvent.emit(RoomUiEvent.ShowWordsDialog(true))
                } else {
                    _uiEvent.emit(RoomUiEvent.ShowWordsDialog(false))
                }

                if (state.currentRoom.gameState is GameState.End) {
                    _uiEvent.emit(
                        RoomUiEvent.ShowVictoryDialog(
                            showDialog = true
                        )
                    )
                }

                if (state.currentRoom.gameState is GameState.Starting) {
                    completedPaths.value = listOf()
                }

            }
        }
    }

    fun observePathData() {
        viewModelScope.launch {
            client.state.collect { state ->
                when (state.drawingEvent) {
                    is DrawingEvent.DrawPath -> handleDrawPath(state.drawingEvent)
                    is DrawingEvent.ToolSelect -> handleToolSelect(state.drawingEvent)
                    else -> {}
                }
            }
        }
    }

    fun handleDrawPath(drawPath: DrawingEvent.DrawPath) {
        when (drawPath.pathData.drawState) {
            DrawState.DrawStart -> {
                val newPath = Path().apply {
                    moveTo(drawPath.pathData.x, drawPath.pathData.y)
                }
                userPaths.value = userPaths.value.toMutableMap().apply {
                    this[drawPath.pathData.drawerId] = PathInfo(
                        path = newPath,
                        color = Color(drawPath.pathData.color),
                        size = drawPath.pathData.size
                    )
                }
            }

            DrawState.Drawing -> {
                val existingPath = userPaths.value[drawPath.pathData.drawerId]
                if (existingPath != null) {
                    val updatedPath = existingPath.path.copy().apply {
                        lineTo(drawPath.pathData.x, drawPath.pathData.y)
                    }
                    userPaths.value = userPaths.value.toMutableMap().apply {
                        this[drawPath.pathData.drawerId] = PathInfo(
                            path = updatedPath,
                            color = Color(drawPath.pathData.color),
                            size = drawPath.pathData.size
                        )
                    }
                }
            }

            DrawState.DrawEnd -> {
                val finishedPath = userPaths.value[drawPath.pathData.drawerId]
                if (finishedPath != null) {
                    completedPaths.value = completedPaths.value.toMutableList().apply {
                        add(finishedPath)
                    }
                }
                userPaths.value = userPaths.value.toMutableMap().apply {
                    remove(drawPath.pathData.drawerId)
                }
            }
        }
    }

    fun handleToolSelect(toolSelect: DrawingEvent.ToolSelect) {

        when (toolSelect.toolType) {
            is ToolType.Eraser -> {

            }

            is ToolType.Undo -> {
                println("Undo")
                completedPaths.value = completedPaths.value.dropLast(1)
            }

            is ToolType.Clear -> {
                completedPaths.value = listOf()
                userPaths.value = mapOf()
            }
        }

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
        currentPath.value = currentPath.value.copy(
            path = currentPath.value.path.copy().apply {
                moveTo(offset.x, offset.y)
            }
        )

        viewModelScope.launch {
            client.sendDrawing(
                DrawingEvent.DrawPath(
                    pathData = PathData(
                        x = offset.x,
                        y = offset.y,
                        color = currentPath.value.color.value,
                        size = currentPath.value.size,
                        drawerId = uiState.value.player.id,
                        drawState = DrawState.DrawStart
                    )
                )
            )
        }
    }

    fun onDrag(change: PointerInputChange, offset: Offset) {
        currentPath.value = currentPath.value.copy(
            path = currentPath.value.path.copy().apply {
                lineTo(change.position.x, change.position.y)
            }
        )
        viewModelScope.launch {
            client.sendDrawing(
                DrawingEvent.DrawPath(
                    pathData = PathData(
                        x = change.position.x,
                        y = change.position.y,
                        color = currentPath.value.color.value,
                        size = currentPath.value.size,
                        drawerId = uiState.value.player.id,
                        drawState = DrawState.Drawing
                    )
                )
            )
        }
    }

    fun onDragEnd() {
        completedPaths.value = completedPaths.value.toMutableStateList().apply {
            add(
                currentPath.value.copy()
            )
        }
        currentPath.value = currentPath.value.copy(
            path = Path()
        )
        viewModelScope.launch {
            client.sendDrawing(
                DrawingEvent.DrawPath(
                    pathData = PathData(
                        x = 0f,
                        y = 0f,
                        color = currentPath.value.color.value,
                        size = currentPath.value.size,
                        drawerId = uiState.value.player.id,
                        drawState = DrawState.DrawEnd
                    )
                )
            )
        }
    }

    fun onColorSelected(color: Color) {
        currentPath.value = currentPath.value.copy(
            color = color
        )
    }

    fun onToolSelected(
        type: ToolType
    ) {

        when (type) {
            is ToolType.Eraser -> {
                currentPath.value = currentPath.value.copy(
                    color = CanvasSurface
                )
            }

            is ToolType.Undo -> {
                completedPaths.value = completedPaths.value.dropLast(1)
            }

            is ToolType.Clear -> {
                completedPaths.value = listOf()
                userPaths.value = mapOf()
            }
        }

        viewModelScope.launch {
            client.sendDrawing(
                DrawingEvent.ToolSelect(
                    toolType = type
                )
            )
        }
    }

    fun onBrushSizeChanged(size: Int) {
        currentPath.value = currentPath.value.copy(
            size = size
        )
    }

    fun sendTransferHost(playerId: String) {
        viewModelScope.launch {
            client.setOwner(playerId)
        }
    }

    fun sendOnKickPlayer(playerId: String) {
        viewModelScope.launch {
            client.kickPlayer(playerId)
        }
    }

    fun sendChangeSettingsRoom(title: String, maxPlayers: Int) {
        viewModelScope.launch {
            client.changeSettingsRoom(title, maxPlayers)
        }
    }

    fun sendFireMessage(messageData: ChatMessageData) {
        viewModelScope.launch {
            client.fireMessage(messageData)
        }
    }

}

data class PathInfo(
    val path: Path,
    val color: Color,
    val size: Int
)

fun pathInfoPlaceHolder() = PathInfo(
    path = Path(),
    color = Color.Black,
    size = 4
)
