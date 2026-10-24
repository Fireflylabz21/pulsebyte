package com.fireflylabz.pulsebyte.data.repository

import com.fireflylabz.pulsebyte.domain.model.NewsArticle
import com.fireflylabz.pulsebyte.domain.model.NewsCluster
import com.fireflylabz.pulsebyte.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.delay

class NewsRepositoryImpl : NewsRepository {

    private val initialClusters = listOf(
        NewsCluster(
            id = "c1",
            title = "Global Climate Summit Reaches Landmark Agreement on Renewable Financing",
            category = "SCIENCE",
            summaryBullets = listOf(
                "Delegates from 195 nations signed a binding treaty for 50B annual green transition fund.",
                "Developing nations secured technology transfer provisions for solar and wind infrastructure.",
                "Enforcement mechanisms will utilize satellite monitoring to verify carbon offset reductions."
            ),
            sourcesCount = 14,
            biasDistribution = mapOf("LEFT" to 0.35f, "CENTER" to 0.45f, "RIGHT" to 0.20f),
            publishedTimeAgo = "25m ago",
            imageUrl = null,
            isSaved = false,
            articles = listOf(
                NewsArticle("a1", "Global Times", "Climate Pact Signed at Geneva Summit", "https://example.com/a1", "LEFT", "30m ago"),
                NewsArticle("a2", "Reuters", "195 Nations Agree on Green Fund Framework", "https://example.com/a2", "CENTER", "45m ago"),
                NewsArticle("a3", "Market Observer", "Economic Realities of New Climate Funding Pledge", "https://example.com/a3", "RIGHT", "1h ago")
            )
        ),
        NewsCluster(
            id = "c2",
            title = "OpenAI Unveils Breakthrough Autonomous Agent Architecture with Real-Time Reasoning",
            category = "TECH",
            summaryBullets = listOf(
                "New multimodal model demonstrates zero-shot execution of complex multi-step software tasks.",
                "Latency reduced by 60% using speculative decoding on custom silicon infrastructure.",
                "Safety guardrails include decentralized adversarial testing before code deployment."
            ),
            sourcesCount = 22,
            biasDistribution = mapOf("LEFT" to 0.25f, "CENTER" to 0.60f, "RIGHT" to 0.15f),
            publishedTimeAgo = "1h ago",
            imageUrl = null,
            isSaved = true,
            articles = listOf(
                NewsArticle("a4", "TechCrunch", "OpenAI's New Agent Paradigm Explained", "https://example.com/a4", "CENTER", "1h ago"),
                NewsArticle("a5", "The Verge", "Inside OpenAI's Real-Time Reasoning Engine", "https://example.com/a5", "LEFT", "2h ago"),
                NewsArticle("a6", "Wired", "Silicon Valley Race for Autonomous AI Accelerates", "https://example.com/a6", "RIGHT", "3h ago")
            )
        ),
        NewsCluster(
            id = "c3",
            title = "Federal Reserve Holds Interest Rates Steady Amid Mixed Inflation Signals",
            category = "FINANCE",
            summaryBullets = listOf(
                "Central bank maintains benchmark rate at 5.25%–5.50% range citing resilient labor market.",
                "Chairman Powell emphasized data-dependent approach for upcoming quarter monetary adjustments.",
                "Wall Street equities rallied moderately following balanced commentary on housing inflation."
            ),
            sourcesCount = 18,
            biasDistribution = mapOf("LEFT" to 0.30f, "CENTER" to 0.50f, "RIGHT" to 0.20f),
            publishedTimeAgo = "2h ago",
            imageUrl = null,
            isSaved = false,
            articles = listOf(
                NewsArticle("a7", "Bloomberg", "Fed Holds Rates Steady as Inflation Cools", "https://example.com/a7", "CENTER", "2h ago"),
                NewsArticle("a8", "Wall Street Journal", "Powell Signals Patience on Rate Cuts", "https://example.com/a8", "RIGHT", "3h ago")
            )
        ),
        NewsCluster(
            id = "c4",
            title = "Major Tech Merger Clears Antitrust Hurdles with Strict Consumer Data Stipulations",
            category = "BUSINESS",
            summaryBullets = listOf(
                "Regulatory bodies approved the $40B acquisition with mandatory open API compliance.",
                "Independent oversight committee appointed to audit user privacy protections quarterly.",
                "Competitors express cautious optimism regarding interoperability mandates."
            ),
            sourcesCount = 9,
            biasDistribution = mapOf("LEFT" to 0.40f, "CENTER" to 0.40f, "RIGHT" to 0.20f),
            publishedTimeAgo = "4h ago",
            imageUrl = null,
            isSaved = false,
            articles = listOf(
                NewsArticle("a9", "Financial Times", "Mega-Merger Wins Antitrust Approval", "https://example.com/a9", "CENTER", "4h ago")
            )
        ),
        NewsCluster(
            id = "c5",
            title = "Bipartisan Infrastructure Bill Passes Senate Focusing on Rural Broadband Expansion",
            category = "POLITICS",
            summaryBullets = listOf(
                "Legislation allocates $15B specifically for fiber-optic expansion in underserved counties.",
                "Passed with an overwhelming 82-15 vote demonstrating rare cross-party consensus.",
                "Implementation scheduled to begin next month with state-level grant distributions."
            ),
            sourcesCount = 15,
            biasDistribution = mapOf("LEFT" to 0.33f, "CENTER" to 0.34f, "RIGHT" to 0.33f),
            publishedTimeAgo = "5h ago",
            imageUrl = null,
            isSaved = false,
            articles = listOf(
                NewsArticle("a10", "Politico", "Senate Passes Broadband Bill in Rare Bipartisan Accord", "https://example.com/a10", "CENTER", "5h ago")
            )
        ),
        NewsCluster(
            id = "c6",
            title = "Global Box Office Hits Record High Driven by Original Sci-Fi Cinematic Releases",
            category = "ENTERTAINMENT",
            summaryBullets = listOf(
                "Weekend ticket sales surpassed $450 million globally across major theater chains.",
                "Audience polling indicates strong preference for original intellectual property over sequels.",
                "Streaming release windows adjusted to 60 days following theatrical success."
            ),
            sourcesCount = 11,
            biasDistribution = mapOf("LEFT" to 0.20f, "CENTER" to 0.70f, "RIGHT" to 0.10f),
            publishedTimeAgo = "6h ago",
            imageUrl = null,
            isSaved = false,
            articles = listOf(
                NewsArticle("a11", "Variety", "Box Office Surge Proves Original Sci-Fi Viability", "https://example.com/a11", "CENTER", "6h ago")
            )
        ),
        NewsCluster(
            id = "c7",
            title = "World Athletics Championship Features Three New World Records in Sprint Events",
            category = "SPORTS",
            summaryBullets = listOf(
                "Innovative track surface technology credited with enhancing athlete velocity and grip.",
                "Young contenders dominated the 100m and 200m finals with stunning closing splits.",
                "Anti-doping agency reports 100% negative results across all medalists."
            ),
            sourcesCount = 16,
            biasDistribution = mapOf("LEFT" to 0.20f, "CENTER" to 0.65f, "RIGHT" to 0.15f),
            publishedTimeAgo = "7h ago",
            imageUrl = null,
            isSaved = false,
            articles = listOf(
                NewsArticle("a12", "ESPN", "Three World Records Shattered at Championship Finals", "https://example.com/a12", "CENTER", "7h ago")
            )
        ),
        NewsCluster(
            id = "c8",
            title = "Breakthrough Longevity Study Links Circadian Sleep Patterns to Cellular Repair",
            category = "LIFESTYLE",
            summaryBullets = listOf(
                "Researchers find synchronized melatonin cycles accelerate cellular autophagy by 30%.",
                "Clinical trial involving 1,200 participants showed sustained metabolic improvements.",
                "Health experts recommend consistent sleep schedules over total sleep duration alone."
            ),
            sourcesCount = 10,
            biasDistribution = mapOf("LEFT" to 0.30f, "CENTER" to 0.50f, "RIGHT" to 0.20f),
            publishedTimeAgo = "8h ago",
            imageUrl = null,
            isSaved = false,
            articles = listOf(
                NewsArticle("a13", "Healthline", "Circadian Rhythm Study Unlocks Cellular Repair Secrets", "https://example.com/a13", "CENTER", "8h ago")
            )
        ),
        NewsCluster(
            id = "c9",
            title = "Metropolitan Transit Authority Launches Autonomous Electric Shuttle Pilot",
            category = "LOCAL",
            summaryBullets = listOf(
                "Zero-emission shuttles connect downtown transit hubs with residential districts.",
                "Fully integrated with city traffic light synchronization for minimal congestion.",
                "Ridership is free during the initial six-month testing phase."
            ),
            sourcesCount = 8,
            biasDistribution = mapOf("LEFT" to 0.40f, "CENTER" to 0.40f, "RIGHT" to 0.20f),
            publishedTimeAgo = "9h ago",
            imageUrl = null,
            isSaved = false,
            articles = listOf(
                NewsArticle("a14", "Local Gazette", "Electric Autonomous Shuttles Begin Downtown Trials", "https://example.com/a14", "CENTER", "9h ago")
            )
        ),
        NewsCluster(
            id = "c10",
            title = "Diplomatic Breakthrough Reached in Regional Trade Negotiations Across Pacific Rim",
            category = "WORLD",
            summaryBullets = listOf(
                "Tariffs on agricultural and tech goods reduced across 12 participating nations.",
                "Supply chain resilience pact established to prevent future logistical bottlenecks.",
                "Joint naval patrols agreed upon to secure vital maritime shipping lanes."
            ),
            sourcesCount = 20,
            biasDistribution = mapOf("LEFT" to 0.30f, "CENTER" to 0.45f, "RIGHT" to 0.25f),
            publishedTimeAgo = "10h ago",
            imageUrl = null,
            isSaved = false,
            articles = listOf(
                NewsArticle("a15", "Foreign Affairs", "Pacific Rim Nations Sign Comprehensive Trade Deal", "https://example.com/a15", "CENTER", "10h ago")
            )
        )
    )

    private val clustersFlow = MutableStateFlow(initialClusters)

    override fun getTrendingClusters(): Flow<List<NewsCluster>> = clustersFlow

    override fun getClustersByCategory(category: String): Flow<List<NewsCluster>> {
        return clustersFlow.map { list ->
            if (category == "All" || category.isEmpty()) list
            else list.filter { it.category.equals(category, ignoreCase = true) }
        }
    }

    override fun getClusterDetail(clusterId: String): Flow<NewsCluster?> {
        return clustersFlow.map { list -> list.find { it.id == clusterId } }
    }

    override fun getSavedClusters(): Flow<List<NewsCluster>> {
        return clustersFlow.map { list -> list.filter { it.isSaved } }
    }

    override suspend fun refreshNewsFeed(): Result<Unit> {
        delay(1200) // Simulate network fetch & Gemini 1.5 Flash batch synthesis
        return Result.success(Unit)
    }

    override suspend fun askAiAboutStory(clusterId: String, query: String): Result<String> {
        delay(1000) // Simulate Gemini 1.5 Flash RAG query response
        val answer = "Based on multi-perspective analysis of this story, $query. The overall coverage spectrum indicates balanced reporting across outlets with primary emphasis on factual event verification."
        return Result.success(answer)
    }

    override suspend fun toggleBookmark(clusterId: String) {
        val current = clustersFlow.value
        val updated = current.map { cluster ->
            if (cluster.id == clusterId) {
                cluster.copy(isSaved = !cluster.isSaved)
            } else {
                cluster
            }
        }
        clustersFlow.value = updated
    }
}
