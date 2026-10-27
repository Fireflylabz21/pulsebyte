package com.fireflylabz.taptap.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fireflylabz.taptap.data.repository.NewsRepositoryImpl
import com.fireflylabz.taptap.domain.model.NewsCluster
import com.fireflylabz.taptap.domain.repository.NewsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface HomeFeedUiState {
    object Loading : HomeFeedUiState
    data class Success(
        val selectedCategory: String = "All",
        val searchQuery: String = "",
        val clusters: List<NewsCluster> = emptyList(),
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
            clusters.filter { it.displayCategory.equals(category, ignoreCase = true) }
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
