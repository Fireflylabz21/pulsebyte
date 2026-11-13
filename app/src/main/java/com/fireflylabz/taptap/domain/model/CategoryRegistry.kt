package com.fireflylabz.taptap.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryTileStyle(
    val title: String,
    val icon: ImageVector,
    val colors: List<Color>
)

data class FlashcardColorScheme(
    val gradientColors: List<Color>,
    val categoryContainer: Color,
    val categoryText: Color,
    val titleText: Color,
    val bulletText: Color,
    val bulletDot: Color
)

object CategoryRegistry {
    val categoriesList = listOf(
        "All",
        "Top 10 News",
        "Politics",
        "Crime",
        "Business",
        "Entertainment",
        "Sports",
        "Health & Wellness",
        "Science & Technology",
        "Weather",
        "Lifestyle & Consumer Advice",
        "Cricket",
        "Enterprise AI",
        "India",
        "Startups & VC",
        "Crypto & Web3",
        "EV & Sustainability",
        "Personal Finance",
        "Global Markets",
        "Gaming & Esports"
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
        "India" to CategoryTileStyle("India", Icons.Filled.Public, listOf(Color(0xFFB45309), Color(0xFF047857))),
        "Startups & VC" to CategoryTileStyle("Startups & VC", Icons.Filled.RocketLaunch, listOf(Color(0xFFBE123C), Color(0xFFE11D48))),
        "Crypto & Web3" to CategoryTileStyle("Crypto & Web3", Icons.Filled.CurrencyBitcoin, listOf(Color(0xFFB45309), Color(0xFFD97706))),
        "EV & Sustainability" to CategoryTileStyle("EV & Sustainability", Icons.Filled.ElectricCar, listOf(Color(0xFF047857), Color(0xFF10B981))),
        "Personal Finance" to CategoryTileStyle("Personal Finance", Icons.Filled.Savings, listOf(Color(0xFF0D9488), Color(0xFF14B8A6))),
        "Global Markets" to CategoryTileStyle("Global Markets", Icons.Filled.Language, listOf(Color(0xFF1D4ED8), Color(0xFF3B82F6))),
        "Gaming & Esports" to CategoryTileStyle("Gaming & Esports", Icons.Filled.SportsEsports, listOf(Color(0xFF6D28D9), Color(0xFF8B5CF6)))
    )

    fun getStyle(category: String): CategoryTileStyle {
        categoryStyles[category]?.let { return it }
        val norm = category.lowercase().trim()
        val matchedKey = categoryStyles.keys.firstOrNull { norm.contains(it.lowercase()) || it.lowercase().contains(norm) }
        return matchedKey?.let { categoryStyles[it] } ?: CategoryTileStyle(
            title = category,
            icon = Icons.Filled.Star,
            colors = listOf(Color(0xFF18181B), Color(0xFF27272A))
        )
    }

    fun getFlashcardColorScheme(category: String): FlashcardColorScheme {
        val norm = category.lowercase().trim()
        return when {
            norm.contains("startup") || norm.contains("vc") -> FlashcardColorScheme(
                gradientColors = listOf(Color(0xFF881337), Color(0xFFBE123C)),
                categoryContainer = Color(0xFFFB7185),
                categoryText = Color(0xFF881337),
                titleText = Color.White,
                bulletText = Color(0xFFFFE4E6),
                bulletDot = Color(0xFFFDA4AF)
            )
            norm.contains("crypto") || norm.contains("web3") || norm.contains("bitcoin") -> FlashcardColorScheme(
                gradientColors = listOf(Color(0xFF78350F), Color(0xFFB45309)),
                categoryContainer = Color(0xFFFBBF24),
                categoryText = Color(0xFF78350F),
                titleText = Color.White,
                bulletText = Color(0xFFFEF3C7),
                bulletDot = Color(0xFFFDE68A)
            )
            norm.contains("ev") || norm.contains("sustainability") || norm.contains("electric") -> FlashcardColorScheme(
                gradientColors = listOf(Color(0xFF064E3B), Color(0xFF047857)),
                categoryContainer = Color(0xFF34D399),
                categoryText = Color(0xFF064E3B),
                titleText = Color.White,
                bulletText = Color(0xFFD1FAE5),
                bulletDot = Color(0xFF6EE7B7)
            )
            norm.contains("personal finance") || norm.contains("saving") || norm.contains("wealth") -> FlashcardColorScheme(
                gradientColors = listOf(Color(0xFF115E59), Color(0xFF0D9488)),
                categoryContainer = Color(0xFF2DD4BF),
                categoryText = Color(0xFF115E59),
                titleText = Color.White,
                bulletText = Color(0xFFCCFBF1),
                bulletDot = Color(0xFF5EEAD4)
            )
            norm.contains("global") || norm.contains("markets") -> FlashcardColorScheme(
                gradientColors = listOf(Color(0xFF1E40AF), Color(0xFF1D4ED8)),
                categoryContainer = Color(0xFF60A5FA),
                categoryText = Color(0xFF1E40AF),
                titleText = Color.White,
                bulletText = Color(0xFFDBEAFE),
                bulletDot = Color(0xFF93C5FD)
            )
            norm.contains("gaming") || norm.contains("esports") -> FlashcardColorScheme(
                gradientColors = listOf(Color(0xFF5B21B6), Color(0xFF6D28D9)),
                categoryContainer = Color(0xFFA78BFA),
                categoryText = Color(0xFF5B21B6),
                titleText = Color.White,
                bulletText = Color(0xFFEDE9FE),
                bulletDot = Color(0xFFC4B5FD)
            )
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
                gradientColors = listOf(Color(0xFF18181B), Color(0xFF27272A)),
                categoryContainer = Color(0xFF38BDF8),
                categoryText = Color(0xFF0F172A),
                titleText = Color.White,
                bulletText = Color(0xFFF4F4F5),
                bulletDot = Color(0xFF38BDF8)
            )
        }
    }
}
