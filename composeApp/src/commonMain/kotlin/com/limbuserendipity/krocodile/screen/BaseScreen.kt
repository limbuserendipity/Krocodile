package com.limbuserendipity.krocodile.screen

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.limbuserendipity.krocodile.component.NotificationDialog
import com.limbuserendipity.krocodile.model.NotificationType
import com.limbuserendipity.krocodile.vm.BaseViewModel

open class BaseScreen(val baseViewModel: BaseViewModel) : Screen {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow

        var notificationMessage by remember {
            mutableStateOf("")
        }

        var showNotificationDialog by remember {
            mutableStateOf(false)
        }

        var notificationType by remember {
            mutableStateOf(NotificationType.ERROR)
        }

        if (showNotificationDialog) {
            NotificationDialog(
                message = notificationMessage,
                messageType = notificationType,
                onDismiss = {
                    showNotificationDialog = false
                }
            )
        }

        LaunchedEffect(baseViewModel.uiEvent) {
            baseViewModel.uiEvent.collect { event ->
                when (event) {
                    UiEvent.Disconnect -> {
                        navigator.popUntilRoot()
                    }

                    UiEvent.NavigateTo -> {

                    }

                    is UiEvent.ShowMessage -> {
                        showNotificationDialog = true
                        notificationMessage = event.message.message
                        notificationType = event.message.type
                    }

                    else -> {}
                }
            }
        }
    }

}