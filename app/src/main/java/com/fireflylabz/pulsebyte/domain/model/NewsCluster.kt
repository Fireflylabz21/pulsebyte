package com.fireflylabz.pulsebyte.domain.model

data class NewsCluster(
    val id: String,
    val title: String,
    val category: String,
    val summaryBullets: List<String>,
    val sourcesCount: Int,
    val biasDistribution: Map<String, Float>,
    val publishedTimeAgo: String,
    val imageUrl: String?,
    val isSaved: Boolean = false,
    val articles: List<NewsArticle> = emptyList()
)

data class NewsArticle(
    val id: String,
    val sourceName: String,
    val originalTitle: String,
    val url: String,
    val biasLabel: String, // "LEFT", "CENTER", "RIGHT"
    val publishedAt: String
)
