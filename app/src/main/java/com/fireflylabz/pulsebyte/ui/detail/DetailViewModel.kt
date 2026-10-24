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
    private val clusterId: String,
    private val repository: NewsRepository = NewsRepositoryImpl()
) : ViewModel() {

    private val _aiResponse = MutableStateFlow<String?>(null)
    private val _isAskingAi = MutableStateFlow(false)

    val uiState: StateFlow<DetailUiState> = combine(
        repository.getClusterDetail(clusterId),
        _aiResponse,
        _isAskingAi
    ) { cluster, response, asking ->
        if (cluster == null) {
            DetailUiState.Error("Story not found")
        } else {
            DetailUiState.Success(
                cluster = cluster,
                aiResponse = response,
                isAskingAi = asking
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DetailUiState.Loading
    )

    fun askAi(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _isAskingAi.value = true
            val result = repository.askAiAboutStory(clusterId, query)
            _aiResponse.value = result.getOrDefault("Could not generate AI response.")
            _isAskingAi.value = false
        }
    }

    fun toggleBookmark() {
        viewModelScope.launch {
            repository.toggleBookmark(clusterId)
        }
    }
}
