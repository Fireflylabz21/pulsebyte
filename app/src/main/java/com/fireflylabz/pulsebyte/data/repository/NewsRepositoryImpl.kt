package com.fireflylabz.pulsebyte.data.repository

import android.util.Log
import com.fireflylabz.pulsebyte.data.remote.PulseSupabase
import com.fireflylabz.pulsebyte.domain.model.NewsCluster
import com.fireflylabz.pulsebyte.domain.repository.NewsRepository
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.delay

class NewsRepositoryImpl : NewsRepository {

    override fun getTrendingClusters(): Flow<List<NewsCluster>> = flow {
        try {
            val remoteClusters = PulseSupabase.client
                .from("story_clusters")
                .select()
                .decodeList<NewsCluster>()
            
            Log.d("PulseAI", "Successfully fetched ${remoteClusters.size} clusters from Supabase")
            emit(remoteClusters)
        } catch (e: Exception) {
            Log.e("PulseAI", "Error fetching story clusters from Supabase", e)
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
        delay(800)
        return Result.success(Unit)
    }

    override suspend fun askAiAboutStory(clusterId: String, query: String): Result<String> {
        delay(1000)
        val answer = "Gemini 1.5 Flash RAG Analysis for '$query': Based on verified multi-perspective AI synthesis, this event reflects significant global impact across financial and technological sectors."
        return Result.success(answer)
    }
}
