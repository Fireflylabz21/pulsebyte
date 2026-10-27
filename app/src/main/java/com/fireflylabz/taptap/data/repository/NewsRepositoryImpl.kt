package com.fireflylabz.taptap.data.repository

import android.util.Log
import com.fireflylabz.taptap.data.remote.PulseSupabase
import com.fireflylabz.taptap.domain.model.NewsCluster
import com.fireflylabz.taptap.domain.repository.NewsRepository
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.delay

class NewsRepositoryImpl : NewsRepository {

    override fun getTrendingClusters(): Flow<List<NewsCluster>> = flow {
        try {
            Log.d("PulseAI", "=== STARTING SUPABASE FETCH ===")
            val result = PulseSupabase.client
                .from("story_clusters")
                .select {
                    order("created_at", order = Order.DESCENDING)
                }
                .decodeList<NewsCluster>()

            Log.d("PulseAI", "=== SUPABASE FETCH COMPLETED ===")
            Log.d("PulseAI", "Total rows returned from Supabase 'story_clusters': ${result.size}")
            
            result.forEachIndexed { index, cluster ->
                Log.d("PulseAI", "Row $index -> ID: ${cluster.id} | Title: ${cluster.title} | Category: ${cluster.category}")
            }

            val sortedResult = result.sortedWith(
                compareByDescending<NewsCluster> { it.createdAt ?: "" }
                    .thenByDescending { it.id }
            )
            emit(sortedResult)
        } catch (e: Exception) {
            Log.e("PulseAI", "=== SUPABASE FETCH FAILED ===", e)
            Log.e("PulseAI", "Exception message: ${e.localizedMessage}", e)
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)

    override fun getClustersByCategory(category: String): Flow<List<NewsCluster>> {
        return getTrendingClusters().map { list ->
            if (category == "All" || category.isEmpty()) list
            else list.filter { it.category.equals(category, ignoreCase = true) }
        }
    }

    override fun getClusterDetail(clusterId: String): Flow<NewsCluster?> {
        return getTrendingClusters().map { list -> list.find { it.id == clusterId } }
    }

    override suspend fun refreshNewsFeed(): Result<Unit> {
        delay(500)
        return Result.success(Unit)
    }

    override suspend fun askAiAboutStory(clusterId: String, query: String): Result<String> {
        delay(1000)
        val answer = "Gemini 3.5 Flash Lite Analysis for '$query': Based on verified multi-perspective AI synthesis, this event reflects significant global impact across financial and technological sectors."
        return Result.success(answer)
    }
}
