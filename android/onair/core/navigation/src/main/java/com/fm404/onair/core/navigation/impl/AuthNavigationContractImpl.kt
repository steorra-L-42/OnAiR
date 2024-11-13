package com.fm404.onair.core.navigation.impl

import android.util.Log
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
        if (navController == null) {
            Log.e("Navigation", "NavController is null in navigateToLogin")
            return
        }

        try {
            navController?.navigate(AuthNavigationContract.ROUTE_LOGIN) {
                popUpTo(0) { inclusive = true }
            }
            Log.d("Navigation", "Successfully navigated to login")
        } catch (e: Exception) {
            Log.e("Navigation", "Failed to navigate to login", e)
        }
//        navController?.navigate(AuthNavigationContract.ROUTE_LOGIN) {
//            popUpTo(0) { inclusive = true }
//        }
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

    override fun navigateToHome() {
        navController?.navigate(AuthNavigationContract.ROUTE_HOME) {
            // 로그인 화면을 백스택에서 제거
            popUpTo(AuthNavigationContract.ROUTE_LOGIN) { inclusive = true }
        }
    }

    override fun navigateToBroadcastList() {
        navController?.navigate(AuthNavigationContract.ROUTE_BROADCAST_LIST) {
            // 로그인 화면을 백스택에서 제거
            popUpTo(AuthNavigationContract.ROUTE_LOGIN) { inclusive = true }
        }
    }
}