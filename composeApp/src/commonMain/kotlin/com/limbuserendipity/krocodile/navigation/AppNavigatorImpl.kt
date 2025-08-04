package com.limbuserendipity.krocodile.navigation

import androidx.navigation.NavHostController

class AppNavigatorImpl() : AppNavigator {
    private var navHostController: NavHostController? = null

    override fun setController(controller: NavHostController) {
        navHostController = controller
    }

    override fun startScreen(): Screen = Screen.SigInScreen

    override fun navigateTo(screen: Screen) {
        navHostController?.navigate(screen)
    }

    override fun navigateBack(): Boolean {
        navHostController?.run {
            return popBackStack()
        }
        return false
    }
}