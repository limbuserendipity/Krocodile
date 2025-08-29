package com.limbuserendipity.krocodile

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cafe.adriel.voyager.navigator.Navigator
import com.limbuserendipity.krocodile.client.GameClient
import com.limbuserendipity.krocodile.module.clientModule
import com.limbuserendipity.krocodile.module.serializationModule
import com.limbuserendipity.krocodile.module.viewModelModule
import com.limbuserendipity.krocodile.screen.SigInScreen
import com.limbuserendipity.krocodile.theme.KrocodileTheme
import org.koin.compose.getKoin
import org.koin.core.context.startKoin

fun main() = application {
    initKoin()
    Window(
        onCloseRequest = ::exitApplication,
        title = "Krocodile",
    ) {
        KrocodileTheme {
            Navigator(SigInScreen())
        }
    }
}

fun initKoin() =
    startKoin {
        modules(
            viewModelModule, clientModule, serializationModule
        )
    }
