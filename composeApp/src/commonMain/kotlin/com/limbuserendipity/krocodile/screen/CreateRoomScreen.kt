package com.limbuserendipity.krocodile.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.limbuserendipity.krocodile.composeApp.commonMain.composeResources.Res
import com.limbuserendipity.krocodile.composeApp.commonMain.composeResources.baseline_arrow_back_ios_24
import com.limbuserendipity.krocodile.util.Space
import com.limbuserendipity.krocodile.vm.CreateRoomViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

class CreateRoomScreen() : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val viewModel: CreateRoomViewModel = koinViewModel()
        val navigator = LocalNavigator.currentOrThrow
        val state = viewModel.createRoomState.collectAsState()

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(32.dp)
        ) {

            var title by remember { mutableStateOf("") }
            var sliderPosition by remember { mutableStateOf(0f) }

            MediumTopAppBar(
                title = {
                    Text(
                        text = "Create Room"
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navigator.pop()
                        }
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.baseline_arrow_back_ios_24),
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = title,
                onValueChange = { value ->
                    title = value
                },
                label = {
                    Text("Room Title")
                }
            )

            64.dp.Space()

            Text(
                text = "Players: ${sliderPosition.toInt()}",
                style = MaterialTheme.typography.bodyMedium
            )

            4.dp.Space()

            Slider(
                value = sliderPosition,
                onValueChange = { value ->
                    sliderPosition = value
                },
                valueRange = 2f..8f,
                steps = 2
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.createRoom(
                        title = title,
                        maxPlayers = sliderPosition.toInt()
                    )
                }
            ) {
                Text(
                    text = "Create",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            LaunchedEffect(state.value) {
                when (state.value) {
                    is CreateRoomState.Failed -> {}
                    is CreateRoomState.Loading -> {}
                    is CreateRoomState.Success -> {
                        navigator.push(RoomScreen())
                    }
                }
            }
        }
    }
}

sealed class CreateRoomState {
    object Loading : CreateRoomState()
    object Success : CreateRoomState()
    data class Failed(val error: String) : CreateRoomState()
}