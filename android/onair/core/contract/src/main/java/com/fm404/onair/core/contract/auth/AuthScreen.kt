package com.fm404.onair.core.contract.auth

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

interface AuthScreen {
    fun NavGraphBuilder.addLoginScreen(navController: NavHostController)
    fun NavGraphBuilder.addRegisterScreen(navController: NavHostController)
    fun NavGraphBuilder.addAdminScreen(navController: NavHostController)
    fun NavGraphBuilder.addSettingsScreen(navController: NavHostController)
}