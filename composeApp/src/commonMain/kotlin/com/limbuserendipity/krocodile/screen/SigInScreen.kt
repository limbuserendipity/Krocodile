package com.limbuserendipity.krocodile.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.limbuserendipity.krocodile.component.SigInForm
import com.limbuserendipity.krocodile.util.Space
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

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
        ) {

            SigInForm(
                ip = ipAddress,
                onIpChange = { ipAddress = it },
                username = username,
                onUsernameChange = { username = it }
            )

            8.dp.Space()

            Button(
                onClick = {
                    if (username.isNotEmpty()) {
                        viewModel.sign(username)
                    }
                }
            ) {
                Text("sig_in")
            }

        }

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