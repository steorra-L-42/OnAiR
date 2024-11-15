package com.fm404.onair.core.navigation.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.fm404.onair.core.common.R
import com.fm404.onair.core.navigation.model.BottomNavItem

@Composable
fun BottomNavBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val bottomNavItems = listOf(
        BottomNavItem("home", R.string.home, R.drawable.house, R.drawable.headphone),
//        BottomNavItem("playlist", R.string.playlist, R.drawable.house, R.drawable.playlistf),
        // other items
        BottomNavItem("settings", R.string.show_more , R.drawable.more, R.drawable.moref)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val pMediumFontFamily = FontFamily(Font(R.font.pmedium))

    NavigationBar(
        containerColor = Color(0xFF0A0C1C),
//        containerColor = Color.White,
//        containerColor = Color(0xFF1211212),
//        containerColor = Color(0xFF121212),
//        contentColor = Color(0xFF0A0C1C),
        modifier = Modifier
            .height(120.dp)
//            .padding(bottom = 10.dp)
            .clip(
                RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                )
            )
            .border(
                width = 1.dp,
                color = Color(0xFF101320),
//                color = Color(0xFFE8EAED),
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                ),
            )
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = currentRoute?.startsWith(item.route) == true
            val iconResId = item.filledIconResId
//            val bottomNavColor = if (isSelected) Color(0xFFB1B8C0) else Color(0xFF3A3F47)
            val bottomNavColor = if (isSelected) Color(0xFF3ead41) else Color(0xFF5A5F67)

            NavigationBarItem(
                icon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Icon(
                            painterResource(id = iconResId),
                            contentDescription = null,
                            tint = bottomNavColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = stringResource(id = item.titleResId),
                            color = bottomNavColor,
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            fontFamily = pMediumFontFamily,
                        )
                    }
                },
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = false
                            }
                            launchSingleTop = true
                            restoreState = false
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF1A1F27),
                    unselectedIconColor = Color(0xFFB1B8C0),
                    indicatorColor = Color.Transparent,
                )
            )
        }
    }
}