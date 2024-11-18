package com.fm404.onair

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fm404.onair.core.contract.auth.AuthNavigationContract
import com.fm404.onair.core.contract.auth.AuthScreen
import com.fm404.onair.core.contract.broadcast.BroadcastNavigationContract
import com.fm404.onair.core.contract.broadcast.BroadcastScreen
import com.fm404.onair.core.contract.statistics.StatisticsNavigationContract
import com.fm404.onair.core.contract.statistics.StatisticsScreen
import com.fm404.onair.core.designsystem.theme.OnAirTheme
import com.fm404.onair.core.navigation.component.BottomNavBar
import com.fm404.onair.core.navigation.graph.MainNavGraph
import com.fm404.onair.core.navigation.model.NavRoute
import com.fm404.onair.core.network.manager.TokenManager
import com.fm404.onair.presentation.main.screen.home.HomeScreenHolder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var homeScreenHolder: HomeScreenHolder

    @Inject
    lateinit var authScreen: AuthScreen

    @Inject
    lateinit var authNavigationContract: AuthNavigationContract

    @Inject
    lateinit var statisticsScreen: StatisticsScreen

    @Inject
    lateinit var statisticsNavigationContract: StatisticsNavigationContract

    @Inject
    lateinit var broadcastScreen: BroadcastScreen

    @Inject
    lateinit var broadcastNavigationContract: BroadcastNavigationContract

    @Inject
    lateinit var tokenManager: TokenManager


    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            // Handle the permissions result
            val granted = permissions.all { it.value }
            if (granted) {
                Toast.makeText(this, "권한이 확인되었어요!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "권한을 허용해야 앱을 이용할 수 있어요.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        checkAndRequestPermissions()
        
        setContent {
            OnAirTheme {
                MainScreen(
                    homeScreen = homeScreenHolder.homeScreen,
                    authScreen = authScreen,
                    authNavigationContract = authNavigationContract,
                    statisticsScreen = statisticsScreen,
                    statisticsNavigationContract = statisticsNavigationContract,
                    broadcastScreen = broadcastScreen,
                    broadcastNavigationContract = broadcastNavigationContract,
                    tokenManager = tokenManager
                )
            }
        }

    }

    private fun checkAndRequestPermissions() {
        val permissions = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (permissions.any {
                ActivityCompat.checkSelfPermission(
                    this,
                    it
                ) != PackageManager.PERMISSION_GRANTED
            }) {

            requestPermissionLauncher.launch(permissions.toTypedArray())
        }
    }
}

@Composable
private fun MainScreen(
    modifier: Modifier = Modifier,
    homeScreen: @Composable (NavHostController) -> Unit,
    authScreen: AuthScreen,
    authNavigationContract: AuthNavigationContract,
    statisticsScreen: StatisticsScreen,
    statisticsNavigationContract: StatisticsNavigationContract,
    broadcastScreen: BroadcastScreen,
    broadcastNavigationContract: BroadcastNavigationContract,
    tokenManager: TokenManager
) {
    val navController = rememberNavController()
    var startDestination by remember { mutableStateOf("") }

    val currentRoute by navController.currentBackStackEntryAsState()

    LaunchedEffect(Unit) {
        startDestination = if (tokenManager.hasValidToken()) {
//            NavRoute.MainSection.Home.route
            "broadcast" // 실제 동작 시 방송 목록이 홈 화면이 되어야함
        } else {
            AuthNavigationContract.GRAPH_AUTH
        }
    }

    LaunchedEffect(navController) {
        authNavigationContract.setNavController(navController)
        statisticsNavigationContract.setNavController(navController)
        broadcastNavigationContract.setNavController(navController)
    }

    // startDestination이 설정된 후에만 NavHost를 표시
    if (startDestination.isNotEmpty()) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            bottomBar = {
                // 현재 route가 login이나 register인 경우 BottomBar 숨김
                val currentDestination = currentRoute?.destination?.route
                if (currentDestination != AuthNavigationContract.ROUTE_LOGIN &&
                    currentDestination != AuthNavigationContract.ROUTE_REGISTER) {
                    BottomNavBar(
                        navController = navController
                    )
                }
//                BottomNavBar( // 테스트를 위해 바텀바가 보이게 함
//                    navController = navController
//                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                MainNavGraph(
                    navController = navController,
                    startDestination = startDestination,
                    homeScreen = homeScreen,
                    authScreen = authScreen,
                    statisticsScreen = statisticsScreen,
                    broadcastScreen = broadcastScreen
                )
            }
        }
    }
}