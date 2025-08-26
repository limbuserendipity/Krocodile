package com.limbuserendipity.krocodile.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.limbuserendipity.krocodile.component.ChatSection
import com.limbuserendipity.krocodile.component.DrawingCanvas
import com.limbuserendipity.krocodile.component.GameRoomHeader
import com.limbuserendipity.krocodile.component.InputSection
import com.limbuserendipity.krocodile.model.GameState
import com.limbuserendipity.krocodile.util.Space
import com.limbuserendipity.krocodile.vm.RoomViewModel
import org.koin.compose.viewmodel.koinViewModel


class RoomScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel: RoomViewModel = koinViewModel()
        var words = remember {
            mutableStateOf(listOf<String>())
        }
        var showDialog by remember {
            mutableStateOf(false)
        }

        val state = viewModel.screenState.collectAsState()

        val roomState = viewModel.roomState.collectAsState()

        val chat = viewModel.chatMessage.collectAsState(listOf())

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
                    roomName = roomState.value!!.roomData.title,
                    playerCount = roomState.value!!.roomData.playerCount,
                    maxPlayers = roomState.value!!.roomData.maxCount,
                    gameStatus = when (roomState.value!!.roomData.gameState) {
                        GameState.Run -> "Идет"
                        GameState.Wait -> "Ожидание"
                    },
                    onShowSettings = {

                    },
                    onStartGame = {

                    },
                    canStart = roomState.value!!.roomData.gameState == GameState.Run,
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
                messages = chat.value,
                modifier = Modifier.align(Alignment.BottomStart)
            )

        }



        if (showDialog) {
            println("showDialog")
            WordsDialog(
                words = words.value,
                onWordClick = { word ->
                    showDialog = false
                    viewModel.sendWordMessage(word)
                }
            )
        }
        LaunchedEffect(state.value) {
            when (state.value) {
                is RoomScreenState.Success -> {
                    println("Success")
                    words.value = (state.value as RoomScreenState.Success).words
                    showDialog = true
                }

                is RoomScreenState.Failed -> {

                }

                RoomScreenState.Loading -> {

                }
            }
        }
    }

    @Composable
    fun WordsDialog(
        words: List<String>,
        onWordClick: (String) -> Unit
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
            shadowElevation = 1.dp
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 32.dp)
            ) {
                2.dp.Space()
                words.forEach { word ->
                    WordItem(
                        word = word,
                        onClick = {
                            onWordClick(word)
                        }
                    )
                    2.dp.Space()
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
                .padding(vertical = 8.dp, horizontal = 16.dp)
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

sealed class RoomScreenState {

    data class Success(
        val words: List<String>
    ) : RoomScreenState()

    object Loading : RoomScreenState()
    data class Failed(val error: String) : RoomScreenState()

}