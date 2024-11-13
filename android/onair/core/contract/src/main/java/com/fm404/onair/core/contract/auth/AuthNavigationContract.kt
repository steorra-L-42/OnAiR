package com.fm404.onair.core.contract.auth

import androidx.navigation.NavHostController

interface NavControllerHolder {
    fun setNavController(navController: NavHostController?)
}

interface AuthNavigationContract : NavControllerHolder {
    fun navigateToRegister()
    fun navigateToLogin()
    fun navigateToAdmin()
    fun navigateToSettings()
    fun navigateBack()
    fun navigateToHome()
    fun navigateToBroadcastList()

    companion object Route {
        const val ROUTE_LOGIN = "login"
        const val ROUTE_REGISTER = "register"
        const val ROUTE_ADMIN = "admin"
        const val ROUTE_SETTINGS = "settings"
        const val GRAPH_AUTH = "auth_graph"
        const val ROUTE_HOME = "home"
        const val ROUTE_BROADCAST_LIST = "broadcast_list"
    }
}