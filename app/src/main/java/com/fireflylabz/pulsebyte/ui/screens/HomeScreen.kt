package com.fireflylabz.pulsebyte.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fireflylabz.pulsebyte.ui.components.NewsClusterCard
import com.fireflylabz.pulsebyte.ui.feed.HomeFeedUiState
import com.fireflylabz.pulsebyte.ui.feed.HomeFeedViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeFeedViewModel,
    onClusterClick: (String) -> Unit,
    onAboutClick: () -> Unit
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "PulseAI News",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                actions = {
                    IconButton(onClick = onAboutClick) {
                        Icon(Icons.Default.Info, contentDescription = "About & Editorial Policy", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = { viewModel.refreshFeed() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh Feed")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is HomeFeedUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is HomeFeedUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                    }
                }
                is HomeFeedUiState.Success -> {
                    // Breaking News Ticker Banner
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.error,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "LIVE",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onError,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Breaking: Global climate summit delegates secure landmark 50B green transition fund.",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                maxLines = 1
                            )
                        }
                    }

                    // Categories Horizontal Scroll
                    val categories = listOf("All", "WORLD", "TECH", "FINANCE", "BUSINESS", "SCIENCE", "POLITICS", "ENTERTAINMENT", "SPORTS", "LIFESTYLE", "LOCAL")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { cat ->
                            val selected = state.selectedCategory.equals(cat, ignoreCase = true)
                            FilterChip(
                                selected = selected,
                                onClick = { viewModel.onCategorySelected(cat) },
                                label = { Text(cat.lowercase().replaceFirstChar { it.uppercase() }) }
                            )
                        }
                    }

                    // Daily 2-Min Audio Briefing Banner Card
                    Card(
                        onClick = {
                            if (isPlayingAudio) {
                                tts?.stop()
                                isPlayingAudio = false
                            } else {
                                val topStory = state.clusters.firstOrNull()
                                val briefingText = if (topStory != null) {
                                    "PulseAI Daily Audio Briefing. Top story: ${topStory.title}. Key takeaways: ${topStory.summaryBullets.joinToString(". ")}"
                                } else {
                                    "Welcome to PulseAI. No stories currently available."
                                }
                                tts?.speak(briefingText, TextToSpeech.QUEUE_FLUSH, null, null)
                                isPlayingAudio = true
                            }
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isPlayingAudio) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = if (isPlayingAudio) Icons.Default.Pause else Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Audio Briefing",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = if (isPlayingAudio) "Playing Daily Briefing..." else "Daily 2-Min Audio Briefing",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "AI Spoken Headlines & Briefing",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (isPlayingAudio) "PAUSE" else "LISTEN",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    if (state.clusters.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No stories found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(state.clusters, key = { it.id }) { cluster ->
                                NewsClusterCard(
                                    cluster = cluster,
                                    onCardClick = { onClusterClick(cluster.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
