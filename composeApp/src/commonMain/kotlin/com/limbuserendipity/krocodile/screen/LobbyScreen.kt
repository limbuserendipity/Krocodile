package com.limbuserendipity.krocodile.screen


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
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
        val lobbyViewModel: LobbyViewModel = koinViewModel()
        val lobbyState = lobbyViewModel.lobbyState.collectAsState()
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
                RoomList(it.rooms)
            }

        }
    }
}