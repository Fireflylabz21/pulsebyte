package com.fireflylabz.taptap.ui.feed

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fireflylabz.taptap.data.remote.PulseSupabase
import com.fireflylabz.taptap.data.repository.NewsRepositoryImpl
import com.fireflylabz.taptap.domain.model.NewsCluster
import com.fireflylabz.taptap.domain.model.matchesCategory
import com.fireflylabz.taptap.domain.repository.NewsRepository
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.decodeRecord
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface HomeFeedUiState {
    object Loading : HomeFeedUiState
    data class Success(
        val selectedCategory: String = "All",
        val searchQuery: String = "",
        val clusters: List<NewsCluster> = emptyList(),
        val allClusters: List<NewsCluster> = emptyList(),
        val isRefreshing: Boolean = false
    ) : HomeFeedUiState
    data class Error(val message: String) : HomeFeedUiState
}

class HomeFeedViewModel(
    private val repository: NewsRepository = NewsRepositoryImpl()
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow("All")
    private val _searchQuery = MutableStateFlow("")
    private val _isRefreshing = MutableStateFlow(false)

    private val _latestBreakingStory = MutableStateFlow<NewsCluster?>(null)
    val latestBreakingStory: StateFlow<NewsCluster?> = _latestBreakingStory.asStateFlow()

    private val _pinnedCategories = MutableStateFlow<Set<String>>(emptySet())
    val pinnedCategories: StateFlow<Set<String>> = _pinnedCategories.asStateFlow()

    init {
        subscribeToBreakingNews()
    }

    private fun subscribeToBreakingNews() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val channel = PulseSupabase.client.channel("public:story_clusters_breaking_feed")
                val changeFlow = channel.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
                    table = "story_clusters"
                }
                channel.subscribe()

                changeFlow.collect { action ->
                    val record = action.decodeRecord<NewsCluster>()
                    if (record.isBreaking) {
                        _latestBreakingStory.value = record
                        Log.d("TapTapRealtime", "Realtime breaking news received: ${record.title}")
                    }
                }
            } catch (e: Exception) {
                Log.e("TapTapRealtime", "Failed to subscribe to realtime breaking news", e)
            }
        }
    }

    fun togglePinCategory(category: String, context: Context) {
        val prefs = context.getSharedPreferences("pulse_prefs", Context.MODE_PRIVATE)
        val current = _pinnedCategories.value.toMutableSet()
        if (current.contains(category)) {
            current.remove(category)
        } else {
            current.add(category)
        }
        _pinnedCategories.value = current
        prefs.edit().putStringSet("pinned_categories", current).apply()
    }

    fun loadPinnedCategories(context: Context) {
        val prefs = context.getSharedPreferences("pulse_prefs", Context.MODE_PRIVATE)
        val saved = prefs.getStringSet("pinned_categories", emptySet()) ?: emptySet()
        _pinnedCategories.value = saved
    }

    // Cache clusters in memory to prevent network refetching on category switching / typing
    private val _rawClusters = repository.getTrendingClusters()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    val uiState: StateFlow<HomeFeedUiState> = combine(
        _selectedCategory,
        _searchQuery,
        _isRefreshing,
        _rawClusters
    ) { category, query, refreshing, clusters ->
        val filteredByCategory = if (category == "All") {
            clusters
        } else {
            clusters.filter { matchesCategory(it.displayCategory, category) }
        }

        // Sort news by latest to oldest
        val sortedByLatest = filteredByCategory.sortedWith(
            compareByDescending<NewsCluster> { it.createdAt ?: "" }
                .thenByDescending { it.id }
        )

        val finalClusters = if (query.isBlank()) {
            sortedByLatest
        } else {
            sortedByLatest.filter { 
                it.displayTitle.contains(query, ignoreCase = true) || 
                it.displayBullets.any { bullet -> bullet.contains(query, ignoreCase = true) }
            }
        }
        HomeFeedUiState.Success(
            selectedCategory = category,
            searchQuery = query,
            clusters = finalClusters,
            allClusters = clusters,
            isRefreshing = refreshing
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeFeedUiState.Loading
    )

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun refreshFeed() {
        viewModelScope.launch {
            _isRefreshing.value = true
            repository.refreshNewsFeed()
            _isRefreshing.value = false
        }
    }
}
