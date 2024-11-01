package com.fm404.onair.core.contract.auth

import androidx.navigation.NavHostController

interface NavControllerHolder {
    fun setNavController(navController: NavHostController?)
}

interface AuthNavigationContract : NavControllerHolder {
    fun navigateToRegister()
    fun navigateToLogin()
    fun navigateBack()

    companion object Route {
        const val ROUTE_LOGIN = "login"
        const val ROUTE_REGISTER = "register"
        const val GRAPH_AUTH = "auth_graph"
    }
}