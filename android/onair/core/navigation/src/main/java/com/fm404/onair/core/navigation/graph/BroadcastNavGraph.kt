package com.fm404.onair.core.navigation.graph

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.fm404.onair.core.contract.broadcast.BroadcastScreen
import com.fm404.onair.core.navigation.model.NavRoute

fun NavGraphBuilder.broadcastNavGraph(
    navController: NavHostController,
    broadcastScreen: BroadcastScreen
) {
    navigation(
        startDestination = NavRoute.BroadcastSection.List.route,
        route = "broadcast"
    ) {
        composable(
            NavRoute.BroadcastSection.List.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            broadcastScreen.BroadcastListRoute(navController)
        }
        composable(
            route = NavRoute.BroadcastSection.Detail.route,
            arguments = listOf(navArgument("broadcastId") { type = NavType.StringType }),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            broadcastScreen.BroadcastDetailRoute(navController)
        }
        composable(
            route = NavRoute.BroadcastSection.Story.route,
            arguments = listOf(navArgument("broadcastId") { type = NavType.StringType }),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            broadcastScreen.StoryRoute(navController)
        }
        composable(
            NavRoute.BroadcastSection.Notification.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            broadcastScreen.NotificationRoute(navController)
        }
        composable(NavRoute.BroadcastSection.Create.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            broadcastScreen.BroadcastCreateRoute(navController)
        }
    }
}