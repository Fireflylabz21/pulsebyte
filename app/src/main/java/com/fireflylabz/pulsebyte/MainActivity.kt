package com.fireflylabz.pulsebyte

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.fireflylabz.pulsebyte.ui.detail.DetailViewModel
import com.fireflylabz.pulsebyte.ui.feed.HomeFeedViewModel
import com.fireflylabz.pulsebyte.ui.screens.DetailScreen
import com.fireflylabz.pulsebyte.ui.screens.HomeScreen
import com.fireflylabz.pulsebyte.ui.screens.SavedScreen
import com.fireflylabz.pulsebyte.ui.theme.PulseByteTheme

class MainActivity : ComponentActivity() {

    private val homeFeedViewModel: HomeFeedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PulseByteTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val showBottomNav = currentRoute == "home" || currentRoute == "saved"

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomNav) {
                            NavigationBar {
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                    label = { Text("Feed") },
                                    selected = currentRoute == "home",
                                    onClick = {
                                        if (currentRoute != "home") {
                                            navController.navigate("home") {
                                                popUpTo("home") { inclusive = true }
                                            }
                                        }
                                    }
                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Bookmark, contentDescription = "Saved") },
                                    label = { Text("Saved") },
                                    selected = currentRoute == "saved",
                                    onClick = {
                                        if (currentRoute != "saved") {
                                            navController.navigate("saved") {
                                                popUpTo("home")
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") {
                            HomeScreen(
                                viewModel = homeFeedViewModel,
                                onClusterClick = { clusterId ->
                                    navController.navigate("detail/$clusterId")
                                }
                            )
                        }
                        composable("saved") {
                            SavedScreen(
                                viewModel = homeFeedViewModel,
                                onClusterClick = { clusterId ->
                                    navController.navigate("detail/$clusterId")
                                }
                            )
                        }
                        composable(
                            route = "detail/{clusterId}",
                            arguments = listOf(navArgument("clusterId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val clusterId = backStackEntry.arguments?.getString("clusterId") ?: ""
                            val detailViewModel = remember(clusterId) { DetailViewModel(clusterId) }
                            DetailScreen(
                                viewModel = detailViewModel,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
