package com.fireflylabz.taptap.domain.repository

import com.fireflylabz.taptap.domain.model.NewsCluster
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getTrendingClusters(): Flow<List<NewsCluster>>
    fun getClustersByCategory(category: String): Flow<List<NewsCluster>>
    fun getClusterDetail(clusterId: String): Flow<NewsCluster?>
    suspend fun refreshNewsFeed(): Result<Unit>
    suspend fun askAiAboutStory(clusterId: String, query: String): Result<String>
}
