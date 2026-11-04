package com.fireflylabz.taptap.ui.jobs

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fireflylabz.taptap.data.remote.JobsSupabase
import com.fireflylabz.taptap.domain.model.JobItem
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface JobsUiState {
    object Loading : JobsUiState
    data class Success(
        val jobs: List<JobItem> = emptyList(),
        val filteredJobs: List<JobItem> = emptyList(),
        val searchQuery: String = "",
        val filterTitle: String = "",
        val filterCompany: String = "",
        val filterLocation: String = "",
        val filterSkillset: String = ""
    ) : JobsUiState
    data class Error(val message: String) : JobsUiState
}

class JobsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<JobsUiState>(JobsUiState.Loading)
    val uiState: StateFlow<JobsUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        fetchJobs()
    }

    fun fetchJobs() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.value = JobsUiState.Loading
                val rawJobs = JobsSupabase.client
                    .from("jobs")
                    .select()
                    .decodeList<JobItem>()

                val activeJobs = rawJobs.filter { it.isActive != false }

                _uiState.value = JobsUiState.Success(
                    jobs = activeJobs,
                    filteredJobs = activeJobs
                )
                Log.d("JobsViewModel", "Successfully fetched ${activeJobs.size} active jobs from Supabase DB table 'jobs'.")
            } catch (e: Exception) {
                Log.e("JobsViewModel", "Failed to fetch jobs from Supabase DB", e)
                _uiState.value = JobsUiState.Error(e.localizedMessage ?: "Failed to load jobs from database.")
            }
        }
    }

    fun refreshJobs() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isRefreshing.value = true
                val rawJobs = JobsSupabase.client
                    .from("jobs")
                    .select()
                    .decodeList<JobItem>()

                val activeJobs = rawJobs.filter { it.isActive != false }

                val currentSuccess = _uiState.value as? JobsUiState.Success
                if (currentSuccess != null) {
                    _uiState.value = currentSuccess.copy(jobs = activeJobs)
                    updateFilters(
                        query = currentSuccess.searchQuery,
                        title = currentSuccess.filterTitle,
                        company = currentSuccess.filterCompany,
                        location = currentSuccess.filterLocation,
                        skillset = currentSuccess.filterSkillset
                    )
                } else {
                    _uiState.value = JobsUiState.Success(
                        jobs = activeJobs,
                        filteredJobs = activeJobs
                    )
                }
            } catch (e: Exception) {
                Log.e("JobsViewModel", "Failed to refresh jobs", e)
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun updateFilters(query: String = "", title: String = "", company: String = "", location: String = "", skillset: String = "") {
        val currentState = _uiState.value as? JobsUiState.Success ?: return

        val filtered = currentState.jobs.filter { job ->
            val skillsText = job.requiredSkills.joinToString(" ")
            val matchesQuery = query.isBlank() || 
                job.title.contains(query, ignoreCase = true) || 
                job.company.contains(query, ignoreCase = true) ||
                skillsText.contains(query, ignoreCase = true) ||
                (job.location?.contains(query, ignoreCase = true) == true) ||
                (job.workStyle?.contains(query, ignoreCase = true) == true) ||
                job.descriptionText.contains(query, ignoreCase = true)

            val matchesTitle = title.isBlank() || job.title.contains(title, ignoreCase = true)
            val matchesCompany = company.isBlank() || job.company.contains(company, ignoreCase = true)
            val matchesLocation = location.isBlank() || (job.location?.contains(location, ignoreCase = true) == true)
            val matchesSkillset = skillset.isBlank() || skillsText.contains(skillset, ignoreCase = true)

            matchesQuery && matchesTitle && matchesCompany && matchesLocation && matchesSkillset
        }

        _uiState.value = currentState.copy(
            filteredJobs = filtered,
            searchQuery = query,
            filterTitle = title,
            filterCompany = company,
            filterLocation = location,
            filterSkillset = skillset
        )
    }
}
