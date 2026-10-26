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
import com.fireflylabz.pulsebyte.ui.feed.HomeFeedViewModel
import com.fireflylabz.pulsebyte.ui.flashcard.StoryFlashcardScreen
import com.fireflylabz.pulsebyte.ui.flashcard.StoryFlashcardViewModel
import com.fireflylabz.pulsebyte.ui.screens.AboutScreen
import com.fireflylabz.pulsebyte.ui.screens.BookmarksScreen
import com.fireflylabz.pulsebyte.ui.screens.HomeScreen
import com.fireflylabz.pulsebyte.ui.screens.WebReaderScreen
import com.fireflylabz.pulsebyte.ui.theme.PulseByteTheme
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

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
                                onCategoryClick = { category ->
                                    val encodedCat = URLEncoder.encode(category, StandardCharsets.UTF_8.toString())
                                    navController.navigate("flashcard/$encodedCat")
                                },
                                onBookmarksClick = {
                                    navController.navigate("bookmarks")
                                },
                                onAboutClick = {
                                    navController.navigate("about")
                                }
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
