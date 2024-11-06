package com.fm404.onair.core.navigation.impl

import androidx.navigation.NavHostController
import com.fm404.onair.core.contract.auth.AuthNavigationContract
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthNavigationContractImpl @Inject constructor() : AuthNavigationContract {
    private var navController: NavHostController? = null

    override fun setNavController(navController: NavHostController?) {
        this.navController = navController
    }

    override fun navigateToRegister() {
        navController?.navigate(AuthNavigationContract.ROUTE_REGISTER)
    }

    override fun navigateToLogin() {
        navController?.navigate(AuthNavigationContract.ROUTE_LOGIN)
    }

    override fun navigateToAdmin() {
        navController?.navigate(AuthNavigationContract.ROUTE_ADMIN)
    }

    override fun navigateToSettings() {
        navController?.navigate(AuthNavigationContract.ROUTE_SETTINGS)
    }

    override fun navigateBack() {
        navController?.popBackStack()
    }
}