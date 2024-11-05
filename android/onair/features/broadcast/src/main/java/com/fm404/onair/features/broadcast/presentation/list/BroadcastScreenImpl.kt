package com.fm404.onair.features.broadcast.presentation.list

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.fm404.onair.core.contract.broadcast.BroadcastNavigationContract
import com.fm404.onair.core.contract.broadcast.BroadcastScreen
import com.fm404.onair.features.broadcast.presentation.detail.screen.BroadcastDetailScreen
import com.fm404.onair.features.broadcast.presentation.list.screen.BroadcastListScreen
import com.fm404.onair.features.broadcast.presentation.story.screen.StoryScreen
import javax.inject.Inject

class BroadcastScreenImpl @Inject constructor() : BroadcastScreen {
    @Composable
    override fun BroadcastListRoute(navController: NavHostController) {
        BroadcastListScreen(
            onBroadcastClick = { broadcastId ->
                navController.navigate(
                    "broadcast_detail/$broadcastId"
                )
            }
        )
    }

    @Composable
    override fun BroadcastDetailRoute(navController: NavHostController) {
        val broadcastId = navController.currentBackStackEntry?.arguments?.getString("broadcastId")
        BroadcastDetailScreen(
            broadcastId = broadcastId.orEmpty(),
            onStoryClick = { storyId ->
                navController.navigate(
                    "broadcast_story/$storyId"
                )
            }
        )
    }

    @Composable
    override fun StoryRoute(navController: NavHostController) {
        val broadcastId = navController.currentBackStackEntry?.arguments?.getString("broadcastId")
        StoryScreen(
            broadcastId = broadcastId.orEmpty(),
            onBackClick = {
                navController.popBackStack()
            }
        )
    }
}