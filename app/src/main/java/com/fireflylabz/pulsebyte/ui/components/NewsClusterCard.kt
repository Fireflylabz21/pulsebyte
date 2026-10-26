package com.fireflylabz.pulsebyte.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fireflylabz.pulsebyte.domain.model.NewsCluster

private data class BannerColorScheme(
    val gradientColors: List<Color>,
    val categoryContainer: Color,
    val categoryText: Color,
    val titleText: Color,
    val bulletText: Color,
    val bulletDot: Color
)

private fun getBannerColorScheme(category: String, index: Int): BannerColorScheme {
    val norm = category.lowercase().trim()
    return when {
        norm.contains("enterprise") || norm.contains("enterprise ai") -> BannerColorScheme(
            gradientColors = listOf(Color(0xFF311B92), Color(0xFF4527A0)),
            categoryContainer = Color(0xFF00E5FF),
            categoryText = Color(0xFF0D47A1),
            titleText = Color.White,
            bulletText = Color(0xFFE8EAF6),
            bulletDot = Color(0xFF80DEEA)
        )
        norm.contains("india") -> BannerColorScheme(
            gradientColors = listOf(Color(0xFFB45309), Color(0xFF047857)),
            categoryContainer = Color(0xFFF59E0B),
            categoryText = Color.White,
            titleText = Color.White,
            bulletText = Color(0xFFFEF3C7),
            bulletDot = Color(0xFFA7F3D0)
        )
        norm.contains("politics") || norm.contains("government") -> BannerColorScheme(
            gradientColors = listOf(Color(0xFF1E3C72), Color(0xFF2A5298)),
            categoryContainer = Color(0xFF3B82F6),
            categoryText = Color.White,
            titleText = Color.White,
            bulletText = Color(0xFFE2E8F0),
            bulletDot = Color(0xFF60A5FA)
        )
        norm.contains("crime") || norm.contains("safety") -> BannerColorScheme(
            gradientColors = listOf(Color(0xFF7F1D1D), Color(0xFFB91C1C)),
            categoryContainer = Color(0xFFEF4444),
            categoryText = Color.White,
            titleText = Color.White,
            bulletText = Color(0xFFFEE2E2),
            bulletDot = Color(0xFFFCA5A5)
        )
        norm.contains("business") || norm.contains("finance") -> BannerColorScheme(
            gradientColors = listOf(Color(0xFF064E3B), Color(0xFF047857)),
            categoryContainer = Color(0xFF10B981),
            categoryText = Color.White,
            titleText = Color.White,
            bulletText = Color(0xFFD1FAE5),
            bulletDot = Color(0xFF34D399)
        )
        norm.contains("entertainment") || norm.contains("celebrity") -> BannerColorScheme(
            gradientColors = listOf(Color(0xFF4C1D95), Color(0xFF6D28D9)),
            categoryContainer = Color(0xFF8B5CF6),
            categoryText = Color.White,
            titleText = Color.White,
            bulletText = Color(0xFFEDE9FE),
            bulletDot = Color(0xFFA78BFA)
        )
        norm.contains("sports") -> BannerColorScheme(
            gradientColors = listOf(Color(0xFF7C2D12), Color(0xFFC2410C)),
            categoryContainer = Color(0xFFF97316),
            categoryText = Color.White,
            titleText = Color.White,
            bulletText = Color(0xFFFFEDD5),
            bulletDot = Color(0xFFFB923C)
        )
        norm.contains("health") || norm.contains("wellness") -> BannerColorScheme(
            gradientColors = listOf(Color(0xFF065F46), Color(0xFF0F766E)),
            categoryContainer = Color(0xFF14B8A6),
            categoryText = Color.White,
            titleText = Color.White,
            bulletText = Color(0xFFCCFBF1),
            bulletDot = Color(0xFF2DD4BF)
        )
        norm.contains("science") || norm.contains("technology") -> BannerColorScheme(
            gradientColors = listOf(Color(0xFF0F172A), Color(0xFF1E293B)),
            categoryContainer = Color(0xFF38BDF8),
            categoryText = Color(0xFF0F172A),
            titleText = Color.White,
            bulletText = Color(0xFFE2E8F0),
            bulletDot = Color(0xFF38BDF8)
        )
        norm.contains("weather") || norm.contains("disaster") -> BannerColorScheme(
            gradientColors = listOf(Color(0xFF1E293B), Color(0xFF334155)),
            categoryContainer = Color(0xFF94A3B8),
            categoryText = Color(0xFF0F172A),
            titleText = Color.White,
            bulletText = Color(0xFFF1F5F9),
            bulletDot = Color(0xFFCBD5E1)
        )
        norm.contains("lifestyle") || norm.contains("consumer") -> BannerColorScheme(
            gradientColors = listOf(Color(0xFF831843), Color(0xFFBE185D)),
            categoryContainer = Color(0xFFEC4899),
            categoryText = Color.White,
            titleText = Color.White,
            bulletText = Color(0xFFFCE7F3),
            bulletDot = Color(0xFFF472B6)
        )
        norm.contains("cricket") -> BannerColorScheme(
            gradientColors = listOf(Color(0xFF14532D), Color(0xFF15803D)),
            categoryContainer = Color(0xFF22C55E),
            categoryText = Color.White,
            titleText = Color.White,
            bulletText = Color(0xFFDCFCE7),
            bulletDot = Color(0xFF4ADE80)
        )
        else -> {
            val palettes = listOf(
                BannerColorScheme(listOf(Color(0xFF1E3C72), Color(0xFF2A5298)), Color(0xFF3B82F6), Color.White, Color.White, Color(0xFFE2E8F0), Color(0xFF60A5FA)),
                BannerColorScheme(listOf(Color(0xFF064E3B), Color(0xFF047857)), Color(0xFF10B981), Color.White, Color.White, Color(0xFFD1FAE5), Color(0xFF34D399)),
                BannerColorScheme(listOf(Color(0xFF4C1D95), Color(0xFF6D28D9)), Color(0xFF8B5CF6), Color.White, Color.White, Color(0xFFEDE9FE), Color(0xFFA78BFA)),
                BannerColorScheme(listOf(Color(0xFF7C2D12), Color(0xFFC2410C)), Color(0xFFF97316), Color.White, Color.White, Color(0xFFFFEDD5), Color(0xFFFB923C)),
                BannerColorScheme(listOf(Color(0xFF831843), Color(0xFFBE185D)), Color(0xFFEC4899), Color.White, Color.White, Color(0xFFFCE7F3), Color(0xFFF472B6))
            )
            palettes[index % palettes.size]
        }
    }
}

@Composable
fun NewsClusterCard(
    cluster: NewsCluster,
    onCardClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    index: Int = 0
) {
    val categoryStr = cluster.displayCategory
    val titleStr = cluster.displayTitle
    val bullets = cluster.displayBullets

    val scheme = remember(categoryStr, index) {
        getBannerColorScheme(categoryStr, index)
    }

    Card(
        onClick = { onCardClick(cluster.id) },
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(scheme.gradientColors))
                .padding(20.dp)
        ) {
            Column {
                // Category Tag
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = scheme.categoryContainer,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = categoryStr.uppercase(),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp),
                            color = scheme.categoryText,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Objective Banner Title
                Text(
                    text = titleStr,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        lineHeight = 25.sp
                    ),
                    color = scheme.titleText
                )

                Spacer(modifier = Modifier.height(14.dp))

                // AI Bullet Points
                bullets.take(3).forEach { bullet ->
                    Row(
                        modifier = Modifier.padding(vertical = 3.dp),
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
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 21.sp),
                            color = scheme.bulletText
                        )
                    }
                }
            }
        }
    }
}
