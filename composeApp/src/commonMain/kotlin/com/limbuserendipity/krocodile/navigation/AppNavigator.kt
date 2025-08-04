package com.limbuserendipity.krocodile.navigation

import androidx.navigation.NavHostController
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object SigInScreen : Screen()
    @Serializable
    data class LobbyScreen(val id: String) : Screen()
    @Serializable
    data object RoomScreen : Screen()
}

interface AppNavigator {
    fun setController(controller: NavHostController)
    fun startScreen(): Screen
    fun navigateTo(screen: Screen)
    fun navigateBack(): Boolean
}