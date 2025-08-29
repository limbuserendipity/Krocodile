package com.limbuserendipity.krocodile.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import com.limbuserendipity.krocodile.component.ChatSection
import com.limbuserendipity.krocodile.component.DrawingCanvas
import com.limbuserendipity.krocodile.component.GameRoomHeader
import com.limbuserendipity.krocodile.component.InputSection
import com.limbuserendipity.krocodile.model.*
import com.limbuserendipity.krocodile.util.Space
import com.limbuserendipity.krocodile.vm.RoomViewModel
import org.koin.compose.viewmodel.koinViewModel


class RoomScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel: RoomViewModel = koinViewModel()
        var showDialog by remember {
            mutableStateOf(false)
        }

        val state = viewModel.uiState.collectAsState()

        val currentPath = viewModel.currentPath.collectAsState()
        val completedPaths = viewModel.completedPaths.collectAsState()
        val usersPath = viewModel.userPaths.collectAsState()

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

                InputSection() { message ->
                    viewModel.sendChatMessage(message)
                }
            }

            ChatSection(
                messages = state.value.chat,
                modifier = Modifier.align(Alignment.BottomStart)
            )

        }

        if (showDialog) {
            println("showDialog")
            WordsDialog(
                words = state.value.availableWords,
                onWordClick = { word ->
                    showDialog = false
                    viewModel.sendWordMessage(word)
                }
            )
        }

        LaunchedEffect(state.value) {
            println("showDialog")
            if (state.value.availableWords.isNotEmpty()) {
                showDialog = true
            }
        }

    }

    @Composable
    fun WordsDialog(
        words: List<String>,
        onWordClick: (String) -> Unit
    ) {
        Dialog(
            onDismissRequest = {

            },
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 32.dp)
            ) {
                4.dp.Space()
                words.forEach { word ->
                    WordItem(
                        word = word,
                        onClick = {
                            onWordClick(word)
                        }
                    )
                    8.dp.Space()
                }
            }
        }

    }

    @Composable
    fun WordItem(
        word: String,
        onClick: () -> Unit
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background)
                .size(100.dp)
                .padding(16.dp)
                .clickable {
                    onClick()
                }
        ) {
            Text(
                text = word,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

data class RoomUiState(
    val roomData: RoomData,
    val players: List<PlayerData>,
    var owner: PlayerData,
    var artist: PlayerData,
    val chat: List<ChatMessageData>,
    val availableWords: List<String>,
    val round: Int = 0
)

fun roomUiStatePlaceHolder() = RoomUiState(
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