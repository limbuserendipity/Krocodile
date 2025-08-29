package com.limbuserendipity.krocodile.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.limbuserendipity.krocodile.component.CreateRoomSurface
import com.limbuserendipity.krocodile.component.LobbyHeader
import com.limbuserendipity.krocodile.component.RoomCard
import com.limbuserendipity.krocodile.model.RoomData
import com.limbuserendipity.krocodile.vm.LobbyViewModel
import org.koin.compose.viewmodel.koinViewModel

class LobbyScreen() : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel: LobbyViewModel = koinViewModel()

        val navigator = LocalNavigator.currentOrThrow

        val state = viewModel.uiState.collectAsState()

        var showDialog by remember {
            mutableStateOf(false)
        }

        Column(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        ) {

            LobbyHeader(
                onCreateRoom = {
                    showDialog = true
                })

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f).padding(top = 8.dp)
            ) {
                items(state.value.rooms) { room ->
                    RoomCard(room = room, onJoin = {
                        viewModel.enterToRoom(room.roomId)
                    })
                }
            }
        }

        LaunchedEffect(viewModel.uiEvent) {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is UiEvent.NavigateToGame -> {
                        navigator.push(RoomScreen())
                    }

                    is UiEvent.NavigateToLobby -> {

                    }

                    is UiEvent.ShowError -> {

                    }

                    is UiEvent.ShowMessage -> {

                    }
                }
            }
        }

        if (showDialog) {
            Dialog(
                onDismissRequest = {
                    showDialog = false
                }
            ) {
                CreateRoomSurface(
                    onCreateRoom = { title, maxPlayers ->
                        viewModel.createRoom(
                            title = title,
                            maxPlayers = maxPlayers
                        )
                    },
                    onDismiss = {
                        showDialog = false
                    }
                )
            }
        }
    }
}

data class LobbyUiState(
    val rooms: List<RoomData> = emptyList(),
)