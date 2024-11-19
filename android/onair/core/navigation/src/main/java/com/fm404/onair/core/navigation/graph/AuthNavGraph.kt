package com.fm404.onair.core.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.fm404.onair.core.contract.auth.AuthNavigationContract
import com.fm404.onair.core.contract.auth.AuthScreen

fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    authScreen: AuthScreen
) {
    navigation(
        startDestination = AuthNavigationContract.ROUTE_LOGIN,
        route = AuthNavigationContract.GRAPH_AUTH
    ) {
        with(authScreen) {
            addLoginScreen(navController)
            addRegisterScreen(navController)
            addAdminScreen(navController)
            addSettingsScreen(navController)
        }
    }
}