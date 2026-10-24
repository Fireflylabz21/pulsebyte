package com.fireflylabz.pulsebyte.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fireflylabz.pulsebyte.data.repository.NewsRepositoryImpl
import com.fireflylabz.pulsebyte.domain.model.NewsCluster
import com.fireflylabz.pulsebyte.domain.repository.NewsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface HomeFeedUiState {
    object Loading : HomeFeedUiState
    data class Success(
        val selectedCategory: String = "All",
        val searchQuery: String = "",
        val clusters: List<NewsCluster> = emptyList(),
        val savedClusters: List<NewsCluster> = emptyList(),
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

    val uiState: StateFlow<HomeFeedUiState> = combine(
        _selectedCategory,
        _searchQuery,
        _isRefreshing,
        repository.getTrendingClusters()
    ) { category, query, refreshing, clusters ->
        val filteredByCategory = if (category == "All") clusters else clusters.filter { it.category.equals(category, ignoreCase = true) }
        val finalClusters = if (query.isBlank()) {
            filteredByCategory
        } else {
            filteredByCategory.filter { 
                it.title.contains(query, ignoreCase = true) || 
                it.summaryBullets.any { bullet -> bullet.contains(query, ignoreCase = true) }
            }
        }
        val saved = clusters.filter { it.isSaved }
        HomeFeedUiState.Success(
            selectedCategory = category,
            searchQuery = query,
            clusters = finalClusters,
            savedClusters = saved,
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

    fun toggleBookmark(clusterId: String) {
        viewModelScope.launch {
            repository.toggleBookmark(clusterId)
        }
    }
}
