package com.fm404.onair.core.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.fm404.onair.core.navigation.model.NavRoute
import com.fm404.onair.features.auth.presentation.login.screen.LoginScreen

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    navigation(
        startDestination = NavRoute.AuthSection.Login.route,
        route = "auth"
    ) {
        composable(route = NavRoute.AuthSection.Login.route) {
            LoginScreen(navController = navController)
        }

//        composable(route = NavRoute.AuthSection.Register.route) {
//             RegisterScreen(navController = navController)
//        }
    }
}