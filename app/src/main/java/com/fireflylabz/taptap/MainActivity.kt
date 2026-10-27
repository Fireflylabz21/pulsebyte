package com.fireflylabz.taptap

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
import com.fireflylabz.taptap.ui.feed.HomeFeedViewModel
import com.fireflylabz.taptap.ui.finance.FinanceHubScreen
import com.fireflylabz.taptap.ui.finance.FinanceViewModel
import com.fireflylabz.taptap.ui.flashcard.StoryFlashcardScreen
import com.fireflylabz.taptap.ui.flashcard.StoryFlashcardViewModel
import com.fireflylabz.taptap.ui.screens.AboutScreen
import com.fireflylabz.taptap.ui.screens.BookmarksScreen
import com.fireflylabz.taptap.ui.screens.HomeScreen
import com.fireflylabz.taptap.ui.screens.SplashScreen
import com.fireflylabz.taptap.ui.screens.WebReaderScreen
import com.fireflylabz.taptap.ui.theme.TapTapTheme
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {

    private val homeFeedViewModel: HomeFeedViewModel by viewModels()
    private val financeViewModel: FinanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            TapTapTheme {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "splash",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("splash") {
                            SplashScreen(
                                onSplashFinished = {
                                    navController.navigate("home") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("home") {
                            HomeScreen(
                                viewModel = homeFeedViewModel,
                                financeViewModel = financeViewModel,
                                onCategoryClick = { category ->
                                    val encodedCat = URLEncoder.encode(category, StandardCharsets.UTF_8.toString())
                                    navController.navigate("flashcard/$encodedCat")
                                },
                                onBookmarksClick = {
                                    navController.navigate("bookmarks")
                                },
                                onAboutClick = {
                                    navController.navigate("about")
                                },
                                onFinanceClick = {
                                    navController.navigate("finance_hub")
                                }
                            )
                        }
                        composable("finance_hub") {
                            FinanceHubScreen(
                                viewModel = financeViewModel,
                                onBackClick = { navController.popBackStack() },
                                onAboutClick = { navController.navigate("about") }
                            )
                        }
                        composable("bookmarks") {
                            BookmarksScreen(
                                viewModel = homeFeedViewModel,
                                onBackClick = { navController.popBackStack() },
                                onClusterClick = { sourceUrl ->
                                    val targetUrl = sourceUrl.takeIf { it.isNotBlank() } ?: "https://news.google.com"
                                    val encodedUrl = URLEncoder.encode(targetUrl, StandardCharsets.UTF_8.toString())
                                    navController.navigate("web_reader/$encodedUrl")
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
                            route = "flashcard/{category}",
                            arguments = listOf(navArgument("category") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val encodedCat = backStackEntry.arguments?.getString("category") ?: ""
                            val decodedCat = URLDecoder.decode(encodedCat, StandardCharsets.UTF_8.toString())
                            val flashcardViewModel = remember(decodedCat) { StoryFlashcardViewModel(decodedCat) }
                            StoryFlashcardScreen(
                                viewModel = flashcardViewModel,
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
                            val decodedUrl = URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8.toString())
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
