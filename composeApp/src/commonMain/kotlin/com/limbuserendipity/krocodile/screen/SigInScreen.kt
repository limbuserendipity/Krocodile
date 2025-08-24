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
        val signState = viewModel.screenState.collectAsState()

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
        
        LaunchedEffect(signState.value) {
            when (signState.value) {
                is ScreenState.Success -> {
                    navigator.push(LobbyScreen())
                }

                is ScreenState.Failed -> {
                    println("Sign in failed: ${(signState.value as ScreenState.Failed).error}")
                }

                is ScreenState.Loading -> {
                }
            }
        }
    }
}

sealed class ScreenState {
    object Loading : ScreenState()
    object Success : ScreenState()
    data class Failed(val error: String) : ScreenState()
}