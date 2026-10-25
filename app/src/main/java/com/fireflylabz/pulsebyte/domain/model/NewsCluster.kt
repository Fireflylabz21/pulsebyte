package com.fireflylabz.pulsebyte.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewsCluster(
    val id: String = "",
    @SerialName("objective_title") val title: String = "",
    val category: String = "WORLD",
    @SerialName("ai_summary_bullets") val summaryBullets: List<String> = emptyList(),
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    val embedding: String? = null,
    val sourcesCount: Int = 3,
    val publishedTimeAgo: String = "1h ago",
    val imageUrl: String? = null
)
