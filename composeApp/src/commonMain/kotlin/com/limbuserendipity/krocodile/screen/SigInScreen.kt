package com.limbuserendipity.krocodile.screen

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.limbuserendipity.krocodile.component.SigInForm
import com.limbuserendipity.krocodile.vm.SigInViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class SigInScreen(val viewModel: SigInViewModel) : BaseScreen(viewModel), KoinComponent {

    @Composable
    override fun Content() {
        super.Content()
        val state = viewModel.uiState.collectAsState()

        val navigator = LocalNavigator.currentOrThrow

        var showDialog by remember {
            mutableStateOf(false)
        }

        SigInForm(
            ipAddress = state.value.ip,
            onIpAddressChange = viewModel::ipAddressUpdate,
            username = state.value.name,
            onUsernameChange = viewModel::usernameUpdate,
            onLogin = viewModel::sign
        )

        if (showDialog) {
            Dialog(
                onDismissRequest = {
                    showDialog = false
                },
                content = {
                    Surface(
                        modifier = Modifier.size(300.dp)
                    ) {
                        Text(
                            text = state.value.message
                        )
                    }
                }
            )
        }
        LaunchedEffect(viewModel.uiEvent) {
            viewModel.uiEvent.collect { event ->
                if (event is SigInUiEvent.NavigateToLobby) {
                    navigator.push(LobbyScreen(get()))
                }
            }
        }
    }
}

sealed class SigInUiEvent : UiEvent() {
    object NavigateToLobby : SigInUiEvent()
}

sealed class UiEvent {
    data class ShowError(val message: String) : UiEvent()
    data class ShowMessage(val message: String) : UiEvent()
    object NavigateTo : UiEvent()

    object Disconnect : UiEvent()
}

data class SigInUiState(
    val ip: String = "",
    val name: String = "",
    val error: String = "",
    val message: String = ""
)