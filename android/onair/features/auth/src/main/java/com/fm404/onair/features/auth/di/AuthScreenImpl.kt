package com.fm404.onair.features.auth.di

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.fm404.onair.core.contract.auth.AuthNavigationContract
import com.fm404.onair.core.contract.auth.AuthScreen
import com.fm404.onair.core.designsystem.theme.OnAirTheme
import com.fm404.onair.features.auth.presentation.admin.screen.AdminScreen
import com.fm404.onair.features.auth.presentation.login.screen.LoginScreen
import com.fm404.onair.features.auth.presentation.register.screen.RegisterScreen
import com.fm404.onair.features.auth.presentation.settings.screen.SettingsScreen
import javax.inject.Inject

class AuthScreenImpl @Inject constructor() : AuthScreen {
    override fun NavGraphBuilder.addLoginScreen(navController: NavHostController) {
        composable(AuthNavigationContract.ROUTE_LOGIN,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            OnAirTheme {
                LoginScreen(navController)
            }
        }
    }

    override fun NavGraphBuilder.addRegisterScreen(navController: NavHostController) {
        composable(AuthNavigationContract.ROUTE_REGISTER,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            OnAirTheme {
                RegisterScreen(navController)
            }
        }
    }

    override fun NavGraphBuilder.addAdminScreen(navController: NavHostController) {
        composable(route = AuthNavigationContract.ROUTE_ADMIN,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            OnAirTheme {
                AdminScreen()
            }
        }
    }

    override fun NavGraphBuilder.addSettingsScreen(navController: NavHostController) {
        composable(route = AuthNavigationContract.ROUTE_SETTINGS,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            OnAirTheme {
                SettingsScreen(navController = navController)
            }
        }
    }
}