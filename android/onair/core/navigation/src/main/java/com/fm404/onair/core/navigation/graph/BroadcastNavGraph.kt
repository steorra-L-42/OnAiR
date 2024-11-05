package com.fm404.onair.core.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.fm404.onair.core.contract.broadcast.BroadcastNavigationContract
import com.fm404.onair.core.contract.broadcast.BroadcastScreen

fun NavGraphBuilder.broadcastNavGraph(
    navController: NavHostController,
    broadcastScreen: BroadcastScreen
) {
    navigation(
        startDestination = BroadcastNavigationContract.ROUTE_HOME,
        route = BroadcastNavigationContract.GRAPH_BROADCAST
    ) {
        with(broadcastScreen){
            addBroadcastHome(navController)
            addBroadcastLive(navController)
            addBroadcastSettings(navController)
        }
    }
}
