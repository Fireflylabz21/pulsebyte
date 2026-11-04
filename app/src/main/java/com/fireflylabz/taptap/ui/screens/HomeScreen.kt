package com.fireflylabz.taptap.ui.screens

import android.content.Intent
import android.net.Uri
import android.speech.tts.TextToSpeech
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fireflylabz.taptap.R
import com.fireflylabz.taptap.domain.model.MarketIndex
import com.fireflylabz.taptap.ui.feed.HomeFeedUiState
import com.fireflylabz.taptap.ui.feed.HomeFeedViewModel
import com.fireflylabz.taptap.ui.finance.FinanceViewModel
import com.fireflylabz.taptap.ui.flashcard.CATEGORIES_LIST
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
    financeViewModel: FinanceViewModel,
    onCategoryClick: (String) -> Unit,
    onBookmarksClick: () -> Unit,
    onAboutClick: () -> Unit,
    onFinanceClick: () -> Unit,
    onJobsClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val indices by financeViewModel.indices.collectAsState()
    val pinnedCategories by viewModel.pinnedCategories.collectAsState()
    val breakingStory by viewModel.latestBreakingStory.collectAsState()
    var showBreakingDialog by remember { mutableStateOf(false) }

    val sensex = indices.firstOrNull { it.symbol == "^BSESN" || it.symbol.contains("SENSEX", ignoreCase = true) }
    val nifty = indices.firstOrNull { it.symbol == "^NSEI" || it.symbol.contains("NIFTY", ignoreCase = true) }
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isPlayingAudio by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadPinnedCategories(context)
    }

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

    val sortedGridCategories = remember(categoryCounts, pinnedCategories) {
        val raw = CATEGORIES_LIST.filter { it != "Top 10 News" }
        val pinned = raw.filter { pinnedCategories.contains(it) }
        val unpinned = raw.filter { !pinnedCategories.contains(it) }.sortedByDescending { cat ->
            categoryCounts.entries.firstOrNull { it.key.equals(cat, ignoreCase = true) }?.value ?: 0
        }
        pinned + unpinned
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AsyncImage(
                            model = R.drawable.app_logo,
                            contentDescription = "Pulsezinga Logo",
                            modifier = Modifier.size(36.dp)
                        )
                        Column {
                            Text(
                                text = "Pulsezinga",
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
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // BusinessHub Button
                        IconButton(
                            onClick = onFinanceClick,
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF18181B), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = "BusinessHub",
                                tint = Color(0xFF34D399),
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

                        // About Us Button
                        IconButton(
                            onClick = onAboutClick,
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF18181B), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "About Us",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    start = paddingValues.calculateLeftPadding(androidx.compose.ui.unit.LayoutDirection.Ltr),
                    end = paddingValues.calculateRightPadding(androidx.compose.ui.unit.LayoutDirection.Ltr),
                    bottom = 0.dp
                ),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Featured Top 10 News Banner Card (With Audio Briefing Button inside)
            item {
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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
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

                                // Audio Briefing Button inside Top 10 News Banner
                                IconButton(
                                    onClick = {
                                        val currentTts = tts
                                        if (currentTts != null) {
                                            if (isPlayingAudio) {
                                                currentTts.stop()
                                                isPlayingAudio = false
                                            } else {
                                                val topStory = activeBannerStory ?: clusters.firstOrNull()
                                                val briefingText = if (topStory != null) {
                                                    "PulseZinga Daily Audio Briefing. Top story: ${topStory.displayTitle}. Key points: ${topStory.displayBullets.joinToString(". ")}"
                                                } else {
                                                    "Welcome to PulseZinga. No stories currently available."
                                                }
                                                currentTts.speak(briefingText, TextToSpeech.QUEUE_FLUSH, null, null)
                                                isPlayingAudio = true
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = if (isPlayingAudio) Icons.Default.Pause else Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Audio Briefing",
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
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
            }

            // 2. Market Ticker Cards for Sensex & Nifty 50
            if (sensex != null || nifty != null) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (sensex != null) {
                            MarketBannerCard(
                                index = sensex,
                                modifier = Modifier.weight(1f),
                                onClick = onFinanceClick
                            )
                        }
                        if (nifty != null) {
                            MarketBannerCard(
                                index = nifty,
                                modifier = Modifier.weight(1f),
                                onClick = onFinanceClick
                            )
                        }
                    }
                }
            }

            // 3. AI Jobs Banner Card (Positioned right below Sensex/Nifty tiles)
            item {
                AiJobsBannerCard(
                    onClick = onJobsClick
                )
            }

            // 4. Section Header: Explore by Category
            item {
                Spacer(modifier = Modifier.height(4.dp))
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
                        modifier = Modifier.clickable { onCategoryClick("All") }
                    )
                }
            }

            // 5. Category Grid Items (Chunked into pairs of 2 per row - Pinned categories first)
            val chunkedCategories = sortedGridCategories.chunked(2)
            items(chunkedCategories) { rowCategories ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowCategories.forEach { category ->
                        val style = categoryStyles[category] ?: CategoryTileStyle(category, Icons.Filled.Star, listOf(Color(0xFF1E3C72), Color(0xFF2A5298)))
                        val isPinned = pinnedCategories.contains(category)

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = if (isPinned) 4.dp else 2.dp),
                            colors = CardDefaults.cardColors(containerColor = if (isPinned) Color(0xFF1E1B4B) else Color(0xFF18181B)),
                            modifier = Modifier
                                .height(84.dp)
                                .weight(1f)
                                .clickable { onCategoryClick(category) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    if (isPinned) {
                                        Text(
                                            text = "PINNED",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                            color = Color(0xFF38BDF8)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                    }
                                    Text(
                                        text = style.title,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            lineHeight = 18.sp
                                        ),
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                
                                IconButton(
                                    onClick = { viewModel.togglePinCategory(category, context) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                        contentDescription = "Pin Category",
                                        tint = if (isPinned) Color(0xFF38BDF8) else Color(0xFFA1A1AA),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                    if (rowCategories.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            // 6. Bottom Banner: WhatsApp Channel Alerts
            item {
                Spacer(modifier = Modifier.height(4.dp))
                WhatsAppAlertsBannerCard(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://whatsapp.com/channel/0029VbDmcRCKAwEls4i4wf1U"))
                        context.startActivity(intent)
                    }
                )
            }
        }
    }

    // Breaking News Dialog Alert
    if (showBreakingDialog && breakingStory != null) {
        AlertDialog(
            onDismissRequest = { showBreakingDialog = false },
            containerColor = Color(0xFF18181B),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(color = Color(0xFF7F1D1D), shape = RoundedCornerShape(6.dp)) {
                        Text("BREAKING", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFFF87171), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                    Text(text = breakingStory?.displayTitle ?: "Breaking News", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    breakingStory?.displayBullets?.forEach { bullet ->
                        Text(text = "• $bullet", style = MaterialTheme.typography.bodyMedium, color = Color(0xFFE2E8F0))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cat = breakingStory?.displayCategory ?: "Top 10 News"
                        showBreakingDialog = false
                        onCategoryClick(cat)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8))
                ) {
                    Text("Explore Story", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBreakingDialog = false }) {
                    Text("Close", color = Color(0xFFA1A1AA))
                }
            }
        )
    }
}

@Composable
fun AiJobsBannerCard(
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF311B92), Color(0xFF4527A0), Color(0xFF7C3AED))
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "⚡ AI JOBS PORTAL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            ),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Explore AI & Tech Careers",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Search top roles by title, company, location, and skillset. Apply instantly in-app.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE2E8F0),
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Surface(
                    color = Color.White,
                    shape = CircleShape
                ) {
                    Box(
                        modifier = Modifier.size(44.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Work,
                            contentDescription = "AI Jobs",
                            tint = Color(0xFF311B92),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WhatsAppAlertsBannerCard(
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF064E3B), Color(0xFF047857), Color(0xFF10B981))
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "💬 WHATSAPP COMMUNITY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            ),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Follow the firefly job Alerts for daily whatsapp alerts",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            lineHeight = 22.sp
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap to join our official WhatsApp channel for instant daily job updates.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFD1FAE5),
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Surface(
                    color = Color.White,
                    shape = CircleShape
                ) {
                    Box(
                        modifier = Modifier.size(44.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Chat,
                            contentDescription = "WhatsApp Channel",
                            tint = Color(0xFF064E3B),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MarketBannerCard(
    index: MarketIndex,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isPositive = index.changePct >= 0
    val isSensex = index.symbol.contains("SENSEX", ignoreCase = true)
    val containerColor = if (isSensex) Color(0xFF131B2E) else Color(0xFF11221C)
    val accentColor = if (isSensex) Color(0xFF60A5FA) else Color(0xFF34D399)

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .height(120.dp)
            .clickable(onClick = onClick)
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
                    text = index.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp),
                    color = Color.White,
                    maxLines = 1
                )
                Surface(
                    color = if (isPositive) Color(0xFF065F46) else Color(0xFF7F1D1D),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "${if (isPositive) "+" else ""}${index.changePct}%",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                        color = if (isPositive) Color(0xFF34D399) else Color(0xFFF87171),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = "%,.2f".format(index.price),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                color = Color.White
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "RSI: ${index.rsi14}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFA1A1AA),
                    fontSize = 11.sp
                )
                Text(
                    text = "Vol: %,d".format(index.volume / 1000000) + "M",
                    style = MaterialTheme.typography.bodySmall,
                    color = accentColor,
                    fontSize = 11.sp
                )
            }
        }
    }
}
