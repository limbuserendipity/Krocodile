package com.limbuserendipity.krocodile.vm

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.lifecycle.viewModelScope
import com.limbuserendipity.krocodile.client.GameClient
import com.limbuserendipity.krocodile.model.*
import com.limbuserendipity.krocodile.screen.RoomUiEvent
import com.limbuserendipity.krocodile.screen.RoomUiState
import com.limbuserendipity.krocodile.screen.roomUiStatePlaceHolder
import com.limbuserendipity.krocodile.theme.CanvasSurface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RoomViewModel(
    client: GameClient
) : BaseViewModel(client) {

    private val _uiState = MutableStateFlow(roomUiStatePlaceHolder())
    val uiState = _uiState.asStateFlow()
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
                        roomSettings = state.settings!!,
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

                if (state.currentRoom.gameState is GameState.Starting && state.player.isArtist) {
                    _uiEvent.emit(RoomUiEvent.ShowWordsDialog(true))
                } else {
                    _uiEvent.emit(RoomUiEvent.ShowWordsDialog(false))
                }

                if (state.currentRoom.gameState is GameState.End) {
                    _uiEvent.emit(
                        RoomUiEvent.ShowEndDialog(
                            showDialog = true
                        )
                    )
                    if ((state.currentRoom.gameState as GameState.End).endVariant is EndVariant.GameEnd) {
                        completedPaths.value = listOf()
                    }
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
                userPaths.value = userPaths.value.toMutableMap().apply {
                    this[drawPath.pathData.drawerId] = PathInfo(
                        points = listOf(Offset(drawPath.pathData.relativeX, drawPath.pathData.relativeY)),
                        color = Color(drawPath.pathData.color),
                        normalizedSize = drawPath.pathData.normalizedSize
                    )
                }
            }

            DrawState.Drawing -> {
                val existingPath = userPaths.value[drawPath.pathData.drawerId]
                if (existingPath != null) {
                    val updatedPoints =
                        existingPath.points + Offset(drawPath.pathData.relativeX, drawPath.pathData.relativeY)
                    userPaths.value = userPaths.value.toMutableMap().apply {
                        this[drawPath.pathData.drawerId] = existingPath.copy(points = updatedPoints)
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

    fun onDragStart(normalizedOffset: Offset) {
        currentPath.value = currentPath.value.copy(
            points = listOf(normalizedOffset)
        )

        viewModelScope.launch {
            client.sendDrawing(
                DrawingEvent.DrawPath(
                    pathData = PathData(
                        relativeX = normalizedOffset.x,
                        relativeY = normalizedOffset.y,
                        color = currentPath.value.color.value,
                        normalizedSize = currentPath.value.normalizedSize,
                        drawerId = uiState.value.player.id,
                        drawState = DrawState.DrawStart
                    )
                )
            )
        }
    }

    fun onDrag(change: PointerInputChange, normalizedOffset: Offset) {
        currentPath.value = currentPath.value.copy(
            points = currentPath.value.points + normalizedOffset
        )

        viewModelScope.launch {
            client.sendDrawing(
                DrawingEvent.DrawPath(
                    pathData = PathData(
                        relativeX = normalizedOffset.x,
                        relativeY = normalizedOffset.y,
                        color = currentPath.value.color.value,
                        normalizedSize = currentPath.value.normalizedSize,
                        drawerId = uiState.value.player.id,
                        drawState = DrawState.Drawing
                    )
                )
            )
        }
    }

    fun onDragEnd() {
        completedPaths.value = completedPaths.value.toMutableList().apply {
            add(currentPath.value)
        }
        currentPath.value = currentPath.value.copy(
            points = emptyList()
        )

        viewModelScope.launch {
            client.sendDrawing(
                DrawingEvent.DrawPath(
                    pathData = PathData(
                        relativeX = 0f,
                        relativeY = 0f,
                        color = currentPath.value.color.value,
                        normalizedSize = currentPath.value.normalizedSize,
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

    fun onBrushSizeChanged(sizeInPixels: Int) {
        val normalizedSize = sizeInPixels.toFloat() / 512f
        currentPath.value = currentPath.value.copy(
            normalizedSize = normalizedSize
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

    fun sendChangeSettingsRoom(title: String, settings: GameRoomSettings) {
        viewModelScope.launch {
            client.changeSettingsRoom(title, settings)
        }
    }

    fun sendFireMessage(messageData: ChatMessageData) {
        viewModelScope.launch {
            client.fireMessage(messageData)
        }
    }

}

data class PathInfo(
    val points: List<Offset>,
    val color: Color,
    val normalizedSize: Float
) {
    fun toScaledPath(canvasSize: Size): Path {
        val path = Path()
        if (points.isNotEmpty()) {
            val first = points[0]
            path.moveTo(first.x * canvasSize.width, first.y * canvasSize.height)
            for (i in 1 until points.size) {
                val point = points[i]
                path.lineTo(point.x * canvasSize.width, point.y * canvasSize.height)
            }
        }
        return path
    }

    fun getActualSize(canvasSize: Size): Float {
        return normalizedSize * minOf(canvasSize.width, canvasSize.height)
    }
}

fun pathInfoPlaceHolder() = PathInfo(
    points = emptyList(),
    color = Color.Black,
    normalizedSize = 8f / 512f
)
