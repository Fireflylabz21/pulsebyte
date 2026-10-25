package com.fireflylabz.pulsebyte

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.fireflylabz.pulsebyte.ui.detail.DetailViewModel
import com.fireflylabz.pulsebyte.ui.feed.HomeFeedViewModel
import com.fireflylabz.pulsebyte.ui.screens.AboutScreen
import com.fireflylabz.pulsebyte.ui.screens.DetailScreen
import com.fireflylabz.pulsebyte.ui.screens.HomeScreen
import com.fireflylabz.pulsebyte.ui.screens.WebReaderScreen
import com.fireflylabz.pulsebyte.ui.theme.PulseByteTheme

class MainActivity : ComponentActivity() {

    private val homeFeedViewModel: HomeFeedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            PulseByteTheme {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize()
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
                                },
                                onAboutClick = {
                                    navController.navigate("about")
                                }
                            )
                        }
                        composable("about") {
                            AboutScreen(
                                onBackClick = {
                                    navController.popBackStack()
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
                                onBackClick = { navController.popBackStack() },
                                onReadOriginalClick = { encodedUrl ->
                                    navController.navigate("web_reader/$encodedUrl")
                                }
                            )
                        }
                        composable(
                            route = "web_reader/{url}",
                            arguments = listOf(navArgument("url") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val encodedUrl = backStackEntry.arguments?.getString("url") ?: ""
                            val decodedUrl = java.net.URLDecoder.decode(encodedUrl, java.nio.charset.StandardCharsets.UTF_8.toString())
                            WebReaderScreen(
                                url = decodedUrl,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
