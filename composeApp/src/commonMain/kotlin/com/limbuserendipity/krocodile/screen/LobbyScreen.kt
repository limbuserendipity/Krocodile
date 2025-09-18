package com.limbuserendipity.krocodile.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.limbuserendipity.krocodile.component.LobbyHeader
import com.limbuserendipity.krocodile.component.RoomCard
import com.limbuserendipity.krocodile.component.RoomSettingsDialog
import com.limbuserendipity.krocodile.model.RoomData
import com.limbuserendipity.krocodile.vm.LobbyViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class LobbyScreen(val viewModel: LobbyViewModel) : BaseScreen(viewModel), KoinComponent {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow

        val state = viewModel.uiState.collectAsState()

        var showDialog by remember {
            mutableStateOf(false)
        }

        var showErrorDialog by remember {
            mutableStateOf(false)
        }

        Column(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        ) {

            LobbyHeader(
                onCreateRoom = {
                    viewModel.onShowCreateRoomDialog()
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
                    is LobbyUiEvent.NavigateToRoom -> {
                        navigator.push(RoomScreen())
                    }

                    is LobbyUiEvent.ShowCreateRoomDialog -> {
                        showDialog = event.showDialog
                    }

                    is UiEvent.NavigateTo -> {
                        navigator.push(SigInScreen(get()))
                    }

                    is UiEvent.ShowError -> {
                        showErrorDialog = true
                    }

                    else -> {

                    }
                }
            }
        }

        if (showDialog) {
            RoomSettingsDialog(
                isCreate = true,
                onDismissRequest = {
                    showDialog = false
                },
                onSettingsRoom = { title, maxPlayers ->
                    viewModel.createRoom(
                        title = title,
                        maxPlayers = maxPlayers
                    )
                })
        }

        if (showErrorDialog) {
            Dialog(
                onDismissRequest = {
                    showErrorDialog = false
                },
            ) {

                Box() {
                    Text(
                        text = "Error"
                    )
                }

            }
        }

    }
}

data class LobbyUiState(
    val rooms: List<RoomData> = emptyList(),
)

sealed class LobbyUiEvent : UiEvent() {
    object NavigateToRoom : LobbyUiEvent()
    data class ShowCreateRoomDialog(
        var showDialog: Boolean
    ) : LobbyUiEvent()
}