package com.fireflylabz.taptap.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fireflylabz.taptap.domain.model.NewsCluster

@Entity(tableName = "news_clusters")
data class NewsClusterEntity(
    @PrimaryKey
    val id: String,
    val objectiveTitle: String?,
    val aiSummaryBulletsJson: String, // JSON serialized bullets
    val category: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val isBreaking: Boolean,
    val readingTimeMinutes: Int,
    val sourceUrl: String?,
    val sourceNames: String?
) {
    fun toDomain(): NewsCluster {
        val bullets = try {
            aiSummaryBulletsJson.split("|||").filter { it.isNotBlank() }
        } catch (e: Exception) {
            emptyList()
        }
        return NewsCluster(
            id = id,
            objectiveTitle = objectiveTitle,
            aiSummaryBullets = bullets,
            categoryField = category,
            createdAtField = createdAt,
            updatedAtField = updatedAt,
            isBreakingField = isBreaking,
            readingTimeMinutesField = readingTimeMinutes,
            sourceUrlField = sourceUrl,
            sourceNamesField = sourceNames
        )
    }

    companion object {
        fun fromDomain(cluster: NewsCluster): NewsClusterEntity {
            val bulletsJson = cluster.summaryBullets.joinToString("|||")
            return NewsClusterEntity(
                id = cluster.id.takeIf { it.isNotBlank() } ?: cluster.sourceUrl.orEmpty(),
                objectiveTitle = cluster.objectiveTitle,
                aiSummaryBulletsJson = bulletsJson,
                category = cluster.categoryField,
                createdAt = cluster.createdAtField,
                updatedAt = cluster.updatedAtField,
                isBreaking = cluster.isBreaking,
                readingTimeMinutes = cluster.readingTimeMinutes,
                sourceUrl = cluster.sourceUrl,
                sourceNames = cluster.sourceNames
            )
        }
    }
}
