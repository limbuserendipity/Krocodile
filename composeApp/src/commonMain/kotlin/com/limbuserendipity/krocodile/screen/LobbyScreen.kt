package com.limbuserendipity.krocodile.screen


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.limbuserendipity.krocodile.component.RoomList
import com.limbuserendipity.krocodile.composeApp.commonMain.composeResources.Res
import com.limbuserendipity.krocodile.composeApp.commonMain.composeResources.baseline_add_24
import com.limbuserendipity.krocodile.vm.LobbyViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

class LobbyScreen() : Screen{

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel: LobbyViewModel = koinViewModel()
        val lobbyState = viewModel.lobbyState.collectAsState()

        val screenState = viewModel.screenState.collectAsState()

        val navigator = LocalNavigator.currentOrThrow

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = "Rooms:"
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            navigator.push(CreateRoomScreen())
                        }
                    ){
                        Icon(
                            painter = painterResource(resource = Res.drawable.baseline_add_24),
                            contentDescription = "add room"
                        )
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )

            )
            lobbyState.value?.let {
                RoomList(
                    rooms = it.rooms,
                    onRoomClick = { roomId ->
                        viewModel.enterToRoom(roomId)
                        navigator.push(RoomScreen())
                    }
                )
            }
        }

        LaunchedEffect(screenState.value) {
            when (screenState.value) {
                is ScreenState.Success -> {
                    navigator.push(RoomScreen())
                }

                is ScreenState.Failed -> {
                    println("Sign in failed: ${(screenState.value as ScreenState.Failed).error}")
                }

                is ScreenState.Loading -> {
                }
            }
        }

    }
}