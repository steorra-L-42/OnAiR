package com.fm404.onair.features.broadcast.presentation.list

import BroadcastLiveScreen
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.fm404.onair.core.contract.broadcast.BroadcastNavigationContract
import com.fm404.onair.core.contract.broadcast.BroadcastScreen
import com.fm404.onair.features.broadcast.presentation.screen.BroadcastHomeScreen
import com.fm404.onair.features.broadcast.presentation.screen.BroadcastSettingsScreen
import javax.inject.Inject

class BroadcastScreenImpl @Inject constructor(
    private val broadcastNavigationContract: BroadcastNavigationContract
) : BroadcastScreen {

    @Composable
    override fun BroadcastHomeRoute(navController: NavHostController) {
        val viewModel: BroadcastListViewModel = hiltViewModel()
        BroadcastHomeScreen(
            navController = navController,
            viewModel = viewModel,
            broadcastNavigationContract = broadcastNavigationContract
        )
    }

    @Composable
    override fun BroadcastLiveRoute(navController: NavHostController) {
        BroadcastLiveScreen(
            navController = navController
        )
    }

    @Composable
    override fun BroadcastSettingsRoute(navController: NavHostController) {
        BroadcastSettingsScreen(
            navController = navController
        )
    }
}
