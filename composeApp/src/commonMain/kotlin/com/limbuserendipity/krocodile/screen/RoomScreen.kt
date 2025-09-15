package com.limbuserendipity.krocodile.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.limbuserendipity.krocodile.component.*
import com.limbuserendipity.krocodile.model.*
import com.limbuserendipity.krocodile.vm.RoomViewModel
import org.koin.compose.viewmodel.koinViewModel


class RoomScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel: RoomViewModel = koinViewModel()

        val state = viewModel.uiState.collectAsState()

        val currentPath = viewModel.currentPath.collectAsState()
        val completedPaths = viewModel.completedPaths.collectAsState()
        val usersPath = viewModel.userPaths.collectAsState()

        var showPlayersDialog by remember {
            mutableStateOf(false)
        }

        var showWordsDialog by remember {
            mutableStateOf(false)
        }

        Box(

        ) {

            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {

                GameRoomHeader(
                    roomName = state.value.roomData.title,
                    playerCount = state.value.players.count(),
                    maxPlayers = state.value.roomData.maxCount,
                    gameStatus = state.value.roomData.gameState,
                    onShowSettings = {

                    },
                    onStartGame = {
                        viewModel.startGame()
                    },
                    canStart = state.value.roomData.playerCount >= 2,
                )

                DrawingCanvas(
                    paddingValues = PaddingValues(24.dp),
                    currentPath = currentPath.value,
                    completedPaths = completedPaths.value,
                    usersPath = usersPath.value.values.toList(),
                    onDragStart = viewModel::onDragStart,
                    onDrag = viewModel::onDrag,
                    onDragEnd = viewModel::onDragEnd,
                    modifier = Modifier.weight(1f)
                )

                InputSection(
                    isArtist = viewModel.uiState.value.player.isArtist,
                    onSendMessage = viewModel::sendChatMessage,
                    onColorSelected = viewModel::onColorSelected,
                    onToolSelected = viewModel::onToolSelected,
                    onBrushSizeChanged = viewModel::onBrushSizeChanged
                )
            }

            ChatSection(
                messages = state.value.chat,
                modifier = Modifier.align(Alignment.BottomStart)
            )

        }

        if (showWordsDialog) {
            WordSelectionDialog(
                words = state.value.availableWords,
                onWordSelected = { word ->
                    showWordsDialog = false
                    viewModel.sendWordMessage(word)
                },
                onDismiss = {

                },
            )
        }

        LaunchedEffect(viewModel.uiEvent) {
            viewModel.uiEvent.collect { event ->
                when (event as RoomUiEvent) {
                    is RoomUiEvent.ShowPlayersDialog -> {

                    }

                    is RoomUiEvent.ShowSettingDialog -> {

                    }

                    is RoomUiEvent.ShowWordsDialog -> {
                        showWordsDialog = (event as RoomUiEvent.ShowWordsDialog).showDialog
                    }
                }
            }
        }

    }
}


data class RoomUiState(
    val player: Player,
    val roomData: RoomData,
    val players: List<PlayerData>,
    var owner: PlayerData,
    var artist: PlayerData,
    var isArtist: Boolean,
    val chat: List<ChatMessageData>,
    val availableWords: List<String>,
    val round: Int = 0
)


fun roomUiStatePlaceHolder() = RoomUiState(
    player = playerPlaceHolder(),
    roomData = RoomData(
        title = "Loading...",
        roomId = 404,
        playerCount = 0,
        maxCount = 0,
        gameState = GameState.Wait
    ),
    players = emptyList(),
    owner = playerDataPlaceHolder(),
    artist = playerDataPlaceHolder(),
    isArtist = false,
    chat = emptyList(),
    availableWords = emptyList(),
    round = 0
)

fun playerPlaceHolder() = Player(
    id = "PlaceHolder",
    name = "Smith",
    isArtist = false,
    roomId = 0
)

fun playerDataPlaceHolder() = PlayerData(
    id = playerPlaceHolder().id,
    name = playerPlaceHolder().name
)

sealed class RoomUiEvent : UiEvent() {

    data class ShowWordsDialog(
        val showDialog: Boolean
    ) : RoomUiEvent()

    data class ShowSettingDialog(
        val showDialog: Boolean
    ) : RoomUiEvent()

    data class ShowPlayersDialog(
        val showDialog: Boolean
    ) : RoomUiEvent()
}