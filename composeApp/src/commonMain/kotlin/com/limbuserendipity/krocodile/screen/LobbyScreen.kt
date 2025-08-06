package com.limbuserendipity.krocodile.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import com.limbuserendipity.krocodile.vm.LobbyViewModel
import org.koin.compose.viewmodel.koinViewModel

data class LobbyScreen(
    val userId : String
) : Screen{

    @Composable
    override fun Content() {
        val lobbyViewModel: LobbyViewModel = koinViewModel()
        Box(
            modifier = Modifier.fillMaxSize(

            )
        ){
            Text(
                text = "Lobby$userId"
            )
        }
    }
}