package com.limbuserendipity.krocodile.screen

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import com.limbuserendipity.krocodile.component.ErrorDialog
import com.limbuserendipity.krocodile.vm.BaseViewModel

open class BaseScreen(val baseViewModel: BaseViewModel) : Screen {

    @Composable
    override fun Content() {
        var showErrorDialog by remember {
            mutableStateOf(false)
        }
        var errorMessage by remember {
            mutableStateOf("")
        }

        if (showErrorDialog) {
            ErrorDialog(
                message = errorMessage,
                onDismiss = {
                    showErrorDialog = false
                }
            )
        }

        LaunchedEffect(baseViewModel.uiEvent) {
            baseViewModel.uiEvent.collect { event ->
                when (event) {
                    UiEvent.Disconnect -> {

                    }

                    UiEvent.NavigateTo -> {

                    }

                    is UiEvent.ShowError -> {
                        if (event.message.isNotEmpty()) {
                            showErrorDialog = true
                            errorMessage = event.message
                        }
                    }

                    is UiEvent.ShowMessage -> {

                    }

                    else -> {}
                }
            }
        }
    }

}