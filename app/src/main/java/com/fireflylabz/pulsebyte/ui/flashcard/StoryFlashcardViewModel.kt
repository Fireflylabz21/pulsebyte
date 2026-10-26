package com.fireflylabz.pulsebyte.ui.flashcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fireflylabz.pulsebyte.data.repository.NewsRepositoryImpl
import com.fireflylabz.pulsebyte.domain.model.NewsCluster
import com.fireflylabz.pulsebyte.domain.repository.NewsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

val CATEGORIES_LIST = listOf(
    "Top 10 News",
    "Politics",
    "Crime",
    "Business",
    "Entertainment",
    "Sports",
    "Health & Wellness",
    "Science & Technology",
    "Weather",
    "Lifestyle & Consumer Advice",
    "Cricket",
    "Enterprise AI",
    "India"
)

sealed interface StoryFlashcardUiState {
    object Loading : StoryFlashcardUiState
    data class Success(
        val currentCategory: String,
        val categoryIndex: Int,
        val currentStoryIndex: Int,
        val currentCluster: NewsCluster?,
        val totalStoriesInCategory: Int,
        val categories: List<String> = CATEG_LIST_VAL
    ) : StoryFlashcardUiState
    data class Empty(val category: String) : StoryFlashcardUiState
}

private val CATEG_LIST_VAL = CATEGORIES_LIST

class StoryFlashcardViewModel(
    initialCategory: String,
    private val repository: NewsRepository = NewsRepositoryImpl()
) : ViewModel() {

    private val _categoryIndex = MutableStateFlow(
        CATEGORIES_LIST.indexOfFirst { it.equals(initialCategory, ignoreCase = true) }.coerceAtLeast(0)
    )
    private val _storyIndex = MutableStateFlow(0)
    private var allClusters: List<NewsCluster> = emptyList()

    init {
        viewModelScope.launch {
            repository.getTrendingClusters().collect { list ->
                allClusters = list
                val stories = getStoriesForCurrentCategory()
                if (_storyIndex.value >= stories.size) {
                    _storyIndex.value = 0
                }
            }
        }
    }

    val uiState: StateFlow<StoryFlashcardUiState> = combine(
        _categoryIndex,
        _storyIndex,
        repository.getTrendingClusters()
    ) { catIdx, storyIdx, clusters ->
        allClusters = clusters
        val category = CATEGORIES_LIST.getOrElse(catIdx) { CATEGORIES_LIST[0] }
        val categoryStories = if (category == "Top 10 News") {
            clusters.take(10)
        } else if (category == "All") {
            clusters
        } else {
            clusters.filter { it.displayCategory.equals(category, ignoreCase = true) }
        }

        if (categoryStories.isEmpty()) {
            StoryFlashcardUiState.Empty(category)
        } else {
            val safeStoryIdx = storyIdx.coerceIn(0, categoryStories.size - 1)
            StoryFlashcardUiState.Success(
                currentCategory = category,
                categoryIndex = catIdx,
                currentStoryIndex = safeStoryIdx,
                currentCluster = categoryStories[safeStoryIdx],
                totalStoriesInCategory = categoryStories.size
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StoryFlashcardUiState.Loading
    )

    private fun getStoriesForCurrentCategory(): List<NewsCluster> {
        val category = CATEGORIES_LIST.getOrElse(_categoryIndex.value) { CATEGORIES_LIST[0] }
        return if (category == "Top 10 News") {
            allClusters.take(10)
        } else {
            allClusters.filter { it.displayCategory.equals(category, ignoreCase = true) }
        }
    }

    fun nextStory() {
        val stories = getStoriesForCurrentCategory()
        if (stories.isNotEmpty() && _storyIndex.value < stories.size - 1) {
            _storyIndex.value++
        }
    }

    fun previousStory() {
        if (_storyIndex.value > 0) {
            _storyIndex.value--
        }
    }

    fun nextCategory() {
        if (_categoryIndex.value < CATEGORIES_LIST.size - 1) {
            _categoryIndex.value++
            _storyIndex.value = 0
        }
    }

    fun previousCategory() {
        if (_categoryIndex.value > 0) {
            _categoryIndex.value--
            _storyIndex.value = 0
        }
    }
}
