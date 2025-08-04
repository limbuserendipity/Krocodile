package com.limbuserendipity.krocodile

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.navigation.compose.rememberNavController
import com.limbuserendipity.krocodile.module.navigationModule
import com.limbuserendipity.krocodile.module.viewModelModule
import com.limbuserendipity.krocodile.navigation.AppNavHost
import com.limbuserendipity.krocodile.navigation.AppNavigatorImpl
import org.koin.compose.getKoin
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun main() = application {
    initKoin()
    Window(
        onCloseRequest = ::exitApplication,
        title = "Krocodile",
    ) {
        MaterialTheme {
            val appNavigator : AppNavigatorImpl = getKoin().get()
            val navController = rememberNavController()
            appNavigator.setController(navController)
            AppNavHost(
                navController = navController,
                startScreen = appNavigator.startScreen()
            )
        }
    }
}

fun initKoin() =
    startKoin {
        modules(
            viewModelModule, navigationModule
        )
    }
