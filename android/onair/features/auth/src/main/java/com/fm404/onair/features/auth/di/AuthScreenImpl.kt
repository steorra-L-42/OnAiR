package com.fm404.onair.features.auth.di

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.fm404.onair.core.contract.auth.AuthNavigationContract
import com.fm404.onair.core.contract.auth.AuthScreen
import com.fm404.onair.features.auth.presentation.admin.screen.AdminScreen
import com.fm404.onair.features.auth.presentation.login.screen.LoginScreen
import com.fm404.onair.features.auth.presentation.register.screen.RegisterScreen
import javax.inject.Inject

class AuthScreenImpl @Inject constructor() : AuthScreen {
    override fun NavGraphBuilder.addLoginScreen(navController: NavHostController) {
        composable(AuthNavigationContract.ROUTE_LOGIN) {
            LoginScreen(navController)
        }
    }

    override fun NavGraphBuilder.addRegisterScreen(navController: NavHostController) {
        composable(AuthNavigationContract.ROUTE_REGISTER) {
            RegisterScreen(navController)
        }
    }

    override fun NavGraphBuilder.addAdminScreen(navController: NavHostController) {
        composable(route = AuthNavigationContract.ROUTE_ADMIN) {
            AdminScreen()
        }
    }
}