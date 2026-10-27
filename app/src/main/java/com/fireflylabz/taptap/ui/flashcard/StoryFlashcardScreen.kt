package com.fireflylabz.taptap.ui.flashcard

import android.content.Context
import android.speech.tts.TextToSpeech
import android.view.HapticFeedbackConstants
import androidx.core.content.edit
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import com.fireflylabz.taptap.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.math.abs

private data class FlashcardColorScheme(
    val gradientColors: List<Color>,
    val categoryContainer: Color,
    val categoryText: Color,
    val titleText: Color,
    val bulletText: Color,
    val bulletDot: Color
)

private fun getFlashcardColorScheme(category: String): FlashcardColorScheme {
    val norm = category.lowercase().trim()
    return when {
        norm.contains("enterprise") || norm.contains("enterprise ai") -> FlashcardColorScheme(
            gradientColors = listOf(Color(0xFF311B92), Color(0xFF4527A0)),
            categoryContainer = Color(0xFF00E5FF),
            categoryText = Color(0xFF0D47A1),
            titleText = Color.White,
            bulletText = Color(0xFFE8EAF6),
            bulletDot = Color(0xFF80DEEA)
        )
        norm.contains("india") -> FlashcardColorScheme(
            gradientColors = listOf(Color(0xFFB45309), Color(0xFF047857)),
            categoryContainer = Color(0xFFF59E0B),
            categoryText = Color.White,
            titleText = Color.White,
            bulletText = Color(0xFFFEF3C7),
            bulletDot = Color(0xFFA7F3D0)
        )
        norm.contains("politics") || norm.contains("government") -> FlashcardColorScheme(
            gradientColors = listOf(Color(0xFF1E3C72), Color(0xFF2A5298)),
            categoryContainer = Color(0xFF3B82F6),
            categoryText = Color.White,
            titleText = Color.White,
            bulletText = Color(0xFFE2E8F0),
            bulletDot = Color(0xFF60A5FA)
        )
        norm.contains("crime") || norm.contains("safety") -> FlashcardColorScheme(
            gradientColors = listOf(Color(0xFF7F1D1D), Color(0xFFB91C1C)),
            categoryContainer = Color(0xFFEF4444),
            categoryText = Color.White,
            titleText = Color.White,
            bulletText = Color(0xFFFEE2E2),
            bulletDot = Color(0xFFFCA5A5)
        )
        norm.contains("business") || norm.contains("finance") -> FlashcardColorScheme(
            gradientColors = listOf(Color(0xFF064E3B), Color(0xFF047857)),
            categoryContainer = Color(0xFF10B981),
            categoryText = Color.White,
            titleText = Color.White,
            bulletText = Color(0xFFD1FAE5),
            bulletDot = Color(0xFF34D399)
        )
        norm.contains("entertainment") || norm.contains("celebrity") -> FlashcardColorScheme(
            gradientColors = listOf(Color(0xFF4C1D95), Color(0xFF6D28D9)),
            categoryContainer = Color(0xFF8B5CF6),
            categoryText = Color.White,
            titleText = Color.White,
            bulletText = Color(0xFFEDE9FE),
            bulletDot = Color(0xFFA78BFA)
        )
        norm.contains("sports") -> FlashcardColorScheme(
            gradientColors = listOf(Color(0xFF7C2D12), Color(0xFFC2410C)),
            categoryContainer = Color(0xFFF97316),
            categoryText = Color.White,
            titleText = Color.White,
            bulletText = Color(0xFFFFEDD5),
            bulletDot = Color(0xFFFB923C)
        )
        norm.contains("health") || norm.contains("wellness") -> FlashcardColorScheme(
            gradientColors = listOf(Color(0xFF065F46), Color(0xFF0F766E)),
            categoryContainer = Color(0xFF14B8A6),
            categoryText = Color.White,
            titleText = Color.White,
            bulletText = Color(0xFFCCFBF1),
            bulletDot = Color(0xFF2DD4BF)
        )
        norm.contains("science") || norm.contains("technology") -> FlashcardColorScheme(
            gradientColors = listOf(Color(0xFF0F172A), Color(0xFF1E293B)),
            categoryContainer = Color(0xFF38BDF8),
            categoryText = Color(0xFF0F172A),
            titleText = Color.White,
            bulletText = Color(0xFFE2E8F0),
            bulletDot = Color(0xFF38BDF8)
        )
        norm.contains("weather") || norm.contains("disaster") -> FlashcardColorScheme(
            gradientColors = listOf(Color(0xFF1E293B), Color(0xFF334155)),
            categoryContainer = Color(0xFF94A3B8),
            categoryText = Color(0xFF0F172A),
            titleText = Color.White,
            bulletText = Color(0xFFF1F5F9),
            bulletDot = Color(0xFFCBD5E1)
        )
        norm.contains("lifestyle") || norm.contains("consumer") -> FlashcardColorScheme(
            gradientColors = listOf(Color(0xFF831843), Color(0xFFBE185D)),
            categoryContainer = Color(0xFFEC4899),
            categoryText = Color.White,
            titleText = Color.White,
            bulletText = Color(0xFFFCE7F3),
            bulletDot = Color(0xFFF472B6)
        )
        norm.contains("cricket") -> FlashcardColorScheme(
            gradientColors = listOf(Color(0xFF14532D), Color(0xFF15803D)),
            categoryContainer = Color(0xFF22C55E),
            categoryText = Color.White,
            titleText = Color.White,
            bulletText = Color(0xFFDCFCE7),
            bulletDot = Color(0xFF4ADE80)
        )
        else -> FlashcardColorScheme(
            gradientColors = listOf(Color(0xFF1E3C72), Color(0xFF2A5298)),
            categoryContainer = Color(0xFF3B82F6),
            categoryText = Color.White,
            titleText = Color.White,
            bulletText = Color(0xFFE2E8F0),
            bulletDot = Color(0xFF60A5FA)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryFlashcardScreen(
    viewModel: StoryFlashcardViewModel,
    onBackClick: () -> Unit,
    onReadOriginalClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val view = LocalView.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isPlayingAudio by remember { mutableStateOf(false) }

    val prefs = remember { context.getSharedPreferences("pulse_prefs", Context.MODE_PRIVATE) }
    var showGestureHint by remember {
        mutableStateOf(!prefs.getBoolean("key_seen_gesture_hint", false))
    }

    val dismissHint = {
        prefs.edit { putBoolean("key_seen_gesture_hint", true) }
        showGestureHint = false
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

    val currentCat = (uiState as? StoryFlashcardUiState.Success)?.currentCategory ?: "News"
    val scheme = remember(currentCat) { getFlashcardColorScheme(currentCat) }
    val cluster = (uiState as? StoryFlashcardUiState.Success)?.currentCluster
    val clusterId = cluster?.id ?: ""

    var isBookmarked by remember(clusterId) {
        mutableStateOf(prefs.getBoolean("bookmark_$clusterId", false))
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF09090B))
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                // Header Row (Back arrow, TapTap title, and Audio button)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Image(
                            painter = painterResource(R.drawable.app_logo),
                            contentDescription = "TapTap Logo",
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "TapTap",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                            color = Color.White
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Audio Briefing Button
                        IconButton(
                            onClick = {
                                if (cluster != null) {
                                    if (isPlayingAudio) {
                                        tts?.stop()
                                        isPlayingAudio = false
                                    } else {
                                        val text = "Story: ${cluster.displayTitle}. Key points: ${cluster.displayBullets.joinToString(". ")}"
                                        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                                        isPlayingAudio = true
                                    }
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
                    }
                }
            }
        },
        containerColor = Color(0xFF09090B)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pointerInput(Unit) {
                    var totalDragX = 0f
                    var totalDragY = 0f
                    detectDragGestures(
                        onDragStart = {
                            totalDragX = 0f
                            totalDragY = 0f
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            totalDragX += dragAmount.x
                            totalDragY += dragAmount.y
                        },
                        onDragEnd = {
                            val swipeThreshold = 60f
                            if (abs(totalDragX) > abs(totalDragY)) {
                                if (totalDragX < -swipeThreshold) {
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                    viewModel.nextCategory()
                                } else if (totalDragX > swipeThreshold) {
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                    viewModel.previousCategory()
                                }
                            } else {
                                if (totalDragY < -swipeThreshold) {
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                    viewModel.nextStory()
                                } else if (totalDragY > swipeThreshold) {
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                    viewModel.previousStory()
                                }
                            }
                        }
                    )
                }
        ) {
            when (val state = uiState) {
                is StoryFlashcardUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF38BDF8))
                    }
                }
                is StoryFlashcardUiState.Empty -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No stories available for '${state.category}'",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFFA1A1AA)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Swipe Left or Right to switch categories",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF38BDF8)
                            )
                        }
                    }
                }
                is StoryFlashcardUiState.Success -> {
                    val activeCluster = state.currentCluster
                    if (activeCluster != null) {
                        // Structured card container matching Home screen layout width & height proportions
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Card(
                                shape = RoundedCornerShape(24.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Brush.linearGradient(scheme.gradientColors))
                                        .padding(22.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            // Category Badge INSIDE the flashcard tile
                                            Surface(
                                                color = scheme.categoryContainer,
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text(
                                                    text = activeCluster.displayCategory.uppercase(),
                                                    style = MaterialTheme.typography.labelLarge.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp,
                                                        letterSpacing = 0.8.sp
                                                    ),
                                                    color = scheme.categoryText,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                                )
                                            }

                                            // Title
                                            Text(
                                                text = activeCluster.displayTitle,
                                                style = MaterialTheme.typography.headlineSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 22.sp,
                                                    lineHeight = 30.sp
                                                ),
                                                color = scheme.titleText
                                            )

                                            // Bullet points
                                            activeCluster.displayBullets.forEach { bullet ->
                                                Row(
                                                    modifier = Modifier.padding(vertical = 4.dp),
                                                    verticalAlignment = Alignment.Top
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .padding(top = 7.dp)
                                                            .size(6.dp)
                                                            .clip(RoundedCornerShape(3.dp))
                                                            .background(scheme.bulletDot)
                                                    )
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    Text(
                                                        text = bullet,
                                                        style = MaterialTheme.typography.bodyLarge.copy(
                                                            fontSize = 15.sp,
                                                            lineHeight = 22.sp
                                                        ),
                                                        color = scheme.bulletText
                                                    )
                                                }
                                            }
                                        }

                                        // Single-Line Bottom Row: Bookmark Icon + Read More Button (Source removed)
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Bookmark / Save Button
                                            IconButton(
                                                onClick = {
                                                    isBookmarked = !isBookmarked
                                                    prefs.edit { putBoolean("bookmark_$clusterId", isBookmarked) }
                                                },
                                                modifier = Modifier
                                                    .size(50.dp)
                                                    .background(Color.Black.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                                            ) {
                                                Icon(
                                                    imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                                    contentDescription = "Bookmark Story",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }

                                            // Read More Button
                                            Button(
                                                onClick = {
                                                    val targetUrl = activeCluster.sourceUrl?.takeIf { it.isNotBlank() } ?: "https://news.google.com"
                                                    val encoded = java.net.URLEncoder.encode(targetUrl, java.nio.charset.StandardCharsets.UTF_8.toString())
                                                    onReadOriginalClick(encoded)
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                                shape = RoundedCornerShape(16.dp),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(50.dp)
                                            ) {
                                                Text(
                                                    text = "Read More →",
                                                    style = MaterialTheme.typography.titleMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 16.sp
                                                    ),
                                                    color = scheme.gradientColors.first()
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Gesture Hint Overlay on First Load Only
            if (showGestureHint) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f))
                        .clickable { dismissHint() },
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "💡 Quick Gestures",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF38BDF8)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "• Swipe Up / Down: Next & Previous Story\n• Swipe Left / Right: Switch Categories",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFFAFAFA)
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = { dismissHint() },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                            ) {
                                Text("Got it", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
