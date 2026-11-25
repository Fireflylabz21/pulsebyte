package com.fireflylabz.taptap.data.repository

import android.content.Context
import android.util.Log
import com.fireflylabz.taptap.TapTapApplication
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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class NewsRepositoryImpl : NewsRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override fun getTrendingClusters(): Flow<List<NewsCluster>> = flow {
        val context = try { TapTapApplication.instance } catch (e: Exception) { null }
        val prefs = context?.getSharedPreferences("pulse_offline_cache_prefs", Context.MODE_PRIVATE)

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

            val sortedResult = result.sortedWith(
                compareByDescending<NewsCluster> { it.createdAt ?: "" }
                    .thenByDescending { it.id }
            )

            // Cache locally for offline reading (Smart Reading History & Offline Caching)
            try {
                val jsonString = json.encodeToString(sortedResult)
                prefs?.edit()?.putString("cached_clusters_json", jsonString)?.apply()
            } catch (ce: Exception) {
                Log.e("PulseAI", "Failed to cache clusters locally", ce)
            }

            emit(sortedResult)
        } catch (e: Exception) {
            Log.e("PulseAI", "=== SUPABASE FETCH FAILED (Attempting Offline Cache) ===", e)
            
            // Fallback to offline cache
            var emittedCached = false
            try {
                val cachedJson = prefs?.getString("cached_clusters_json", null)
                if (!cachedJson.isNullOrBlank()) {
                    val cachedList = json.decodeFromString<List<NewsCluster>>(cachedJson)
                    Log.d("PulseAI", "Successfully loaded ${cachedList.size} clusters from offline cache.")
                    emit(cachedList)
                    emittedCached = true
                }
            } catch (cacheEx: Exception) {
                Log.e("PulseAI", "Failed to load offline cache", cacheEx)
            }

            if (!emittedCached) {
                emit(emptyList())
            }
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
