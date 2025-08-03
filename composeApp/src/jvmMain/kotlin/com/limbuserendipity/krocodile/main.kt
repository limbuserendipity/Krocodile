package com.limbuserendipity.krocodile

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.limbuserendipity.krocodile.module.viewModelModule
import com.limbuserendipity.krocodile.screen.SigInScreen
import com.limbuserendipity.krocodile.vm.SigInViewModel
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.context.KoinContext
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun main() = application {
    initKoin {  }
    Window(
        onCloseRequest = ::exitApplication,
        title = "Krocodile",
    ) {
        MaterialTheme {
            SigInScreen(koinViewModel<SigInViewModel>())
        }
    }
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        modules(
            viewModelModule
        )
    }
