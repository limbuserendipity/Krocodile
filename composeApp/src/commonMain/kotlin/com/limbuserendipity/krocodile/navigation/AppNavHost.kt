package com.limbuserendipity.krocodile.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.limbuserendipity.krocodile.screen.SigInScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    startScreen: Screen
) {
    NavHost(
        navController = navController,
        startDestination = startScreen
    ) {
        composable<Screen.SigInScreen>(){
            SigInScreen()
        }
    }
}