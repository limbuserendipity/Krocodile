package com.limbuserendipity.krocodile.screen

import PlayersListDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.limbuserendipity.krocodile.component.*
import com.limbuserendipity.krocodile.model.*
import com.limbuserendipity.krocodile.vm.RoomViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class RoomScreen(val viewModel: RoomViewModel) : BaseScreen(viewModel), KoinComponent {
    @Composable
    override fun Content() {

        super.Content()

        val state = viewModel.uiState.collectAsState()

        val navigator = LocalNavigator.currentOrThrow

        val currentPath = viewModel.currentPath.collectAsState()
        val completedPaths = viewModel.completedPaths.collectAsState()
        val usersPath = viewModel.userPaths.collectAsState()

        var showPlayersDialog by remember {
            mutableStateOf(false)
        }

        var showWordsDialog by remember {
            mutableStateOf(false)
        }

        var showSettingDialog by remember {
            mutableStateOf(false)
        }

        var showEndDialog by remember {
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
                    round = state.value.round,
                    artistName = state.value.players.firstOrNull { it.isArtist }?.name ?: "Игрок",
                    gameStatus = state.value.roomData.gameState,
                    isOwner = state.value.owner.id == state.value.player.id,
                    onPlayers = {
                        showPlayersDialog = true
                    },
                    onShowSettings = {
                        showSettingDialog = true
                    },
                    onStartGame = {
                        viewModel.startGame()
                    },
                    canStart = state.value.roomData.playerCount >= 2,
                )

                DrawingCanvas(
                    paddingValues = PaddingValues(24.dp),
                    isArtist = state.value.isArtist,
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

            var isChatExpanded by remember { mutableStateOf(false) }

            ExpandableChatFab(
                messages = state.value.chat,
                isExpanded = isChatExpanded,
                onToggle = { isChatExpanded = !isChatExpanded },
                onFireMessage = { message ->
                    if (state.value.player.isArtist) {
                        viewModel.sendFireMessage(message)
                    }
                },
                modifier = Modifier.align(Alignment.BottomEnd)
            )

        }

        if (showWordsDialog) {
            WordSelectionDialog(
                words = state.value.availableWords,
                onWordSelected = { word ->
                    showWordsDialog = false
                    viewModel.sendWordMessage(word)
                },
                time = (state.value.roomData.gameState as GameState.Starting).time.toFloat(),
                onDismiss = {

                },
            )
        }

        if (showPlayersDialog) {
            PlayersListDialog(
                players = state.value.players,
                currentUserId = state.value.player.id,
                ownerId = state.value.owner.id,
                onTransferHost = { playerId ->
                    viewModel.sendTransferHost(playerId)
                },
                onKickPlayer = { playerId ->
                    viewModel.sendOnKickPlayer(playerId)
                },
                onDismissRequest = {
                    showPlayersDialog = false
                }
            )
        }

        if (showSettingDialog) {
            RoomSettingsDialog(
                isCreate = false,
                roomName = state.value.roomData.title,
                roomSettings = state.value.roomSettings,
                onDismissRequest = {
                    showSettingDialog = false
                },
                onSettingsRoom = { title, maxPlayers, maxRounds, maxTime, difficulty ->
                    viewModel.sendChangeSettingsRoom(
                        title,
                        settings = GameRoomSettings(
                            maxPlayers = maxPlayers,
                            maxRounds = maxRounds,
                            maxTime = maxTime,
                            difficulty = difficulty
                        ),
                    )
                    showSettingDialog = false
                }
            )
        }

        if (showEndDialog && state.value.roomData.gameState is GameState.End) {

            EndDialog(
                endState = state.value.roomData.gameState as GameState.End,
                paths = viewModel.completedPaths.value,

                onDismissRequest = {
                    showEndDialog = false
                },
                players = state.value.players,

                closeAnswers = state.value.chat.sortedBy { it.fire }.take(3)
            )

        }

        LaunchedEffect(viewModel.uiEvent) {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is RoomUiEvent.ShowEndDialog -> {
                        showEndDialog = event.showDialog
                    }

                    is RoomUiEvent.ShowWordsDialog -> {

                        showWordsDialog = event.showDialog
                    }

                    is RoomUiEvent.NavigateToLobby -> {
                        navigator.push(LobbyScreen(get()))
                    }

                    else -> {

                    }
                }

            }
        }
    }
}


data class RoomUiState(
    val player: Player,
    val roomData: RoomData,
    val roomSettings: GameRoomSettings,
    val players: List<PlayerData>,
    var owner: PlayerData,
    var artist: PlayerData,
    var isArtist: Boolean,
    val chat: List<ChatMessageData>,
    val availableWords: List<String>,
    val round: Int
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
    roomSettings = gameRoomSettingsPlaceholder(),
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
    roomId = 0,
    score = 0
)

fun playerDataPlaceHolder() = PlayerData(
    id = playerPlaceHolder().id,
    name = playerPlaceHolder().name,
    score = 0,
    isArtist = false
)

sealed class RoomUiEvent : UiEvent() {

    data class ShowWordsDialog(
        val showDialog: Boolean
    ) : RoomUiEvent()

    data class ShowEndDialog(
        val showDialog: Boolean
    ) : RoomUiEvent()

    object NavigateToLobby : RoomUiEvent()

}