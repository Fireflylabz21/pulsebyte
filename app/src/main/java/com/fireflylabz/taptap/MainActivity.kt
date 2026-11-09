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
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.fireflylabz.taptap.ui.feed.HomeFeedViewModel
import com.fireflylabz.taptap.ui.finance.FinanceHubScreen
import com.fireflylabz.taptap.ui.finance.FinanceViewModel
import com.fireflylabz.taptap.ui.flashcard.StoryFlashcardScreen
import com.fireflylabz.taptap.ui.flashcard.StoryFlashcardViewModel
import com.fireflylabz.taptap.ui.jobs.JobsScreen
import com.fireflylabz.taptap.ui.jobs.JobsViewModel
import com.fireflylabz.taptap.ui.screens.AboutScreen
import com.fireflylabz.taptap.ui.screens.BookmarksScreen
import com.fireflylabz.taptap.ui.screens.HomeScreen
import com.fireflylabz.taptap.ui.screens.SplashScreen
import com.fireflylabz.taptap.ui.screens.WebReaderScreen
import com.fireflylabz.taptap.ui.theme.TapTapTheme
import com.fireflylabz.taptap.util.DailySummaryWorker
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private val homeFeedViewModel: HomeFeedViewModel by viewModels()
    private val financeViewModel: FinanceViewModel by viewModels()
    private val jobsViewModel: JobsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
        scheduleDailyWork()

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
                                },
                                onJobsClick = {
                                    navController.navigate("jobs")
                                }
                            )
                        }
                        composable("jobs") {
                            JobsScreen(
                                viewModel = jobsViewModel,
                                onBackClick = { navController.popBackStack() },
                                onAboutClick = { navController.navigate("about") },
                                onApplyClick = { applyUrl ->
                                    val encodedUrl = URLEncoder.encode(applyUrl, StandardCharsets.UTF_8.toString())
                                    navController.navigate("web_reader/$encodedUrl")
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
                                },
                                onPrivacyClick = { url ->
                                    val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                                    navController.navigate("web_reader/$encodedUrl")
                                }
                            )
                        }
                        composable(
                            route = "flashcard/{category}",
                            arguments = listOf(navArgument("category") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val encodedCat = backStackEntry.arguments?.getString("category") ?: ""
                            val decodedCat = URLDecoder.decode(encodedCat, StandardCharsets.UTF_8.toString())
                            val flashcardViewModel: StoryFlashcardViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                                factory = StoryFlashcardViewModel.Factory(decodedCat)
                            )
                            StoryFlashcardScreen(
                                viewModel = flashcardViewModel,
                                onBackClick = { navController.popBackStack() },
                                onReadOriginalClick = { encodedUrl ->
                                    navController.navigate("web_reader/$encodedUrl")
                                },
                                onAboutClick = {
                                    navController.navigate("about")
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

    private fun scheduleDailyWork() {
        val workManager = WorkManager.getInstance(this)
        
        scheduleAt(workManager, 10, 0, "DailySummary10AM")
        scheduleAt(workManager, 20, 0, "DailySummary8PM")
    }

    private fun scheduleAt(workManager: WorkManager, hour: Int, minute: Int, tag: String) {
        val currentTime = Calendar.getInstance()
        val dueTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(currentTime)) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }
        
        val initialDelay = dueTime.timeInMillis - currentTime.timeInMillis
        
        val workRequest = PeriodicWorkRequestBuilder<DailySummaryWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .addTag(tag)
            .build()
            
        workManager.enqueueUniquePeriodicWork(
            tag,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
}
