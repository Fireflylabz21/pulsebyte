package com.fireflylabz.pulsebyte.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fireflylabz.pulsebyte.data.repository.NewsRepositoryImpl
import com.fireflylabz.pulsebyte.domain.model.NewsCluster
import com.fireflylabz.pulsebyte.domain.repository.NewsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface DetailUiState {
    object Loading : DetailUiState
    data class Success(
        val cluster: NewsCluster,
        val aiResponse: String? = null,
        val isAskingAi: Boolean = false
    ) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

class DetailViewModel(
    private val initialClusterId: String,
    private val repository: NewsRepository = NewsRepositoryImpl()
) : ViewModel() {

    private var allClusters: List<NewsCluster> = emptyList()
    private val _currentIndex = MutableStateFlow(0)
    private val _aiResponse = MutableStateFlow<String?>(null)
    private val _isAskingAi = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            repository.getTrendingClusters().collect { list ->
                allClusters = list
                val idx = list.indexOfFirst { it.id == initialClusterId }
                if (idx != -1) {
                    _currentIndex.value = idx
                }
            }
        }
    }

    val uiState: StateFlow<DetailUiState> = combine(
        _currentIndex,
        _aiResponse,
        _isAskingAi
    ) { index, response, asking ->
        if (allClusters.isEmpty()) {
            DetailUiState.Loading
        } else {
            val safeIndex = index.coerceIn(0, allClusters.size - 1)
            DetailUiState.Success(
                cluster = allClusters[safeIndex],
                aiResponse = response,
                isAskingAi = asking
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DetailUiState.Loading
    )

    fun moveToNext() {
        if (allClusters.isNotEmpty() && _currentIndex.value < allClusters.size - 1) {
            _currentIndex.value++
            _aiResponse.value = null
        }
    }

    fun moveToPrevious() {
        if (allClusters.isNotEmpty() && _currentIndex.value > 0) {
            _currentIndex.value--
            _aiResponse.value = null
        }
    }

    fun askAi(query: String) {
        if (query.isBlank()) return
        val currentCluster = allClusters.getOrNull(_currentIndex.value) ?: return
        viewModelScope.launch {
            _isAskingAi.value = true
            val result = repository.askAiAboutStory(currentCluster.id, query)
            _aiResponse.value = result.getOrDefault("Could not generate AI response.")
            _isAskingAi.value = false
        }
    }
}
