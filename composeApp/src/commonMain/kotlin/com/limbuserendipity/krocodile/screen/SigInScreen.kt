package com.limbuserendipity.krocodile.screen

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.limbuserendipity.krocodile.component.SigInForm
import com.limbuserendipity.krocodile.vm.SigInViewModel
import org.koin.compose.viewmodel.koinViewModel

class SigInScreen() : Screen {

    @Composable
    override fun Content() {
        val viewModel: SigInViewModel = koinViewModel()
        val navigator = LocalNavigator.currentOrThrow

        var ipAddress by remember {
            mutableStateOf("")
        }
        var username by remember {
            mutableStateOf("")
        }

        SigInForm(
            ipAddress = ipAddress,
            onIpAddressChange = {
                ipAddress = it
            },
            username = username,
            onUsernameChange = {
                username = it
            },
            onLogin = {
                if (username.isNotEmpty()) {
                    viewModel.sign(username)
                }
            }
        )

        LaunchedEffect(viewModel.uiEvent) {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is UiEvent.NavigateToGame -> {

                    }

                    is UiEvent.NavigateToLobby -> {
                        navigator.push(LobbyScreen())
                    }

                    is UiEvent.ShowError -> {

                    }

                    is UiEvent.ShowMessage -> {

                    }
                }
            }
        }
    }
}

sealed class UiEvent {
    data class ShowError(val message: String) : UiEvent()
    data class ShowMessage(val message: String) : UiEvent()
    object NavigateToGame : UiEvent()
    object NavigateToLobby : UiEvent()
}