package com.fireflylabz.pulsebyte.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fireflylabz.pulsebyte.ui.feed.HomeFeedUiState
import com.fireflylabz.pulsebyte.ui.feed.HomeFeedViewModel
import com.fireflylabz.pulsebyte.ui.flashcard.CATEGORIES_LIST
import kotlinx.coroutines.delay
import java.util.Locale

private data class CategoryTileStyle(
    val title: String,
    val icon: ImageVector,
    val colors: List<Color>
)

private val categoryStyles = mapOf(
    "Top 10 News" to CategoryTileStyle("Top 10 News", Icons.Filled.Star, listOf(Color(0xFF833AB4), Color(0xFFFD1D1D), Color(0xFFFCB045))),
    "Politics" to CategoryTileStyle("Politics", Icons.Filled.AccountBalance, listOf(Color(0xFF1E3C72), Color(0xFF2A5298))),
    "Crime" to CategoryTileStyle("Crime", Icons.Filled.Gavel, listOf(Color(0xFF7F1D1D), Color(0xFFB91C1C))),
    "Business" to CategoryTileStyle("Business", Icons.Filled.BusinessCenter, listOf(Color(0xFF064E3B), Color(0xFF047857))),
    "Entertainment" to CategoryTileStyle("Entertainment", Icons.Filled.Movie, listOf(Color(0xFF4C1D95), Color(0xFF6D28D9))),
    "Sports" to CategoryTileStyle("Sports", Icons.Filled.SportsSoccer, listOf(Color(0xFF7C2D12), Color(0xFFC2410C))),
    "Health & Wellness" to CategoryTileStyle("Health & Wellness", Icons.Filled.LocalHospital, listOf(Color(0xFF065F46), Color(0xFF0F766E))),
    "Science & Technology" to CategoryTileStyle("Science & Tech", Icons.Filled.Science, listOf(Color(0xFF0F172A), Color(0xFF1E293B))),
    "Weather" to CategoryTileStyle("Weather", Icons.Filled.WbSunny, listOf(Color(0xFF1E293B), Color(0xFF334155))),
    "Lifestyle & Consumer Advice" to CategoryTileStyle("Lifestyle", Icons.Filled.EmojiEmotions, listOf(Color(0xFF831843), Color(0xFFBE185D))),
    "Cricket" to CategoryTileStyle("Cricket", Icons.Filled.SportsCricket, listOf(Color(0xFF14532D), Color(0xFF15803D))),
    "Enterprise AI" to CategoryTileStyle("Enterprise AI", Icons.Filled.SmartToy, listOf(Color(0xFF311B92), Color(0xFF4527A0))),
    "India" to CategoryTileStyle("India", Icons.Filled.Public, listOf(Color(0xFFB45309), Color(0xFF047857)))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeFeedViewModel,
    onCategoryClick: (String) -> Unit,
    onBookmarksClick: () -> Unit,
    onAboutClick: () -> Unit,
    onFinanceClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isPlayingAudio by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
            }
        }
        tts = ttsInstance
        onDispose {
            ttsInstance.stop()
            ttsInstance.shutdown()
        }
    }

    val clusters = (uiState as? HomeFeedUiState.Success)?.clusters ?: emptyList()
    val categoryCounts = remember(clusters) {
        clusters.groupingBy { it.displayCategory.trim() }.eachCount()
    }

    val sortedGridCategories = remember(categoryCounts) {
        CATEGORIES_LIST
            .filter { it != "Top 10 News" }
            .sortedByDescending { cat ->
                categoryCounts.entries.firstOrNull { it.key.equals(cat, ignoreCase = true) }?.value ?: 0
            }
    }

    val top10Stories = remember(clusters) { clusters.take(10) }
    var bannerIndex by remember { mutableStateOf(0) }

    if (top10Stories.isNotEmpty()) {
        LaunchedEffect(top10Stories) {
            while (true) {
                delay(4500L)
                bannerIndex = (bannerIndex + 1) % top10Stories.size
            }
        }
    }

    val activeBannerStory = top10Stories.getOrNull(bannerIndex)

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF09090B))
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                // Header Row (Zero top spacing, flush to top)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TapTap",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp
                            ),
                            color = Color.White
                        )
                        Text(
                            text = "News that fits your day",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFA1A1AA)
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Finance Hub Button
                        IconButton(
                            onClick = onFinanceClick,
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF18181B), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = "Finance Hub",
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Audio Briefing Button
                        IconButton(
                            onClick = {
                                if (isPlayingAudio) {
                                    tts?.stop()
                                    isPlayingAudio = false
                                } else {
                                    val topStory = activeBannerStory ?: clusters.firstOrNull()
                                    val briefingText = if (topStory != null) {
                                        "TapTap Daily Audio Briefing. Top story: ${topStory.displayTitle}. Key points: ${topStory.displayBullets.joinToString(". ")}"
                                    } else {
                                        "Welcome to TapTap. No stories currently available."
                                    }
                                    tts?.speak(briefingText, TextToSpeech.QUEUE_FLUSH, null, null)
                                    isPlayingAudio = true
                                }
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF18181B), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isPlayingAudio) Icons.Default.Pause else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Audio Briefing",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Bookmark Button
                        IconButton(
                            onClick = onBookmarksClick,
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF18181B), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Bookmark,
                                contentDescription = "Saved Stories",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFF09090B)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Featured Top 10 News of the Day Banner Card (Rotating through Top 10 Stories)
            Card(
                shape = RoundedCornerShape(22.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clickable { onCategoryClick("Top 10 News") }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF833AB4), Color(0xFFFD1D1D), Color(0xFFFCB045))
                            )
                        )
                        .padding(20.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "🔥 TOP 10 NEWS OF THE DAY (${bannerIndex + 1}/10)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                ),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        AnimatedContent(
                            targetState = activeBannerStory,
                            transitionSpec = {
                                (fadeIn(animationSpec = tween(500)) + slideInVertically(animationSpec = tween(500)) { height -> height / 2 })
                                    .togetherWith(fadeOut(animationSpec = tween(500)) + slideOutVertically(animationSpec = tween(500)) { height -> -height / 2 })
                            },
                            label = "BannerStoryRotation"
                        ) { story ->
                            Column {
                                Text(
                                    text = story?.displayTitle ?: "Earth's Climate Crosses Key Tipping Point, Warn Scientists",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        lineHeight = 24.sp
                                    ),
                                    color = Color.White,
                                    maxLines = 2
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${story?.displayCategory ?: "General"} • Tap to Explore",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFE2E8F0)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section Header: Explore by Category
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Explore by Category",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                    color = Color.White
                )
                Text(
                    text = "See All",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF38BDF8),
                    modifier = Modifier.clickable { onCategoryClick("Top 10 News") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(sortedGridCategories) { category ->
                    val count = categoryCounts.entries.firstOrNull { it.key.equals(category, ignoreCase = true) }?.value ?: 0
                    val style = categoryStyles[category] ?: CategoryTileStyle(category, Icons.Filled.Star, listOf(Color(0xFF1E3C72), Color(0xFF2A5298)))
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                        modifier = Modifier
                            .height(100.dp)
                            .fillMaxWidth()
                            .clickable { onCategoryClick(category) }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = style.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        lineHeight = 20.sp
                                    ),
                                    color = Color.White,
                                    maxLines = 1,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = style.icon,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Text(
                                text = "$count stories",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFA1A1AA)
                            )
                        }
                    }
                }
            }
        }
    }
}
