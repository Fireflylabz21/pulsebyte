package com.fireflylabz.taptap.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JobItem(
    val id: String,
    val title: String,
    val company: String,
    val location: String? = null,
    @SerialName("work_style")
    val workStyle: String? = null,
    @SerialName("min_experience")
    val minExperience: Int? = null,
    @SerialName("max_experience")
    val maxExperience: Int? = null,
    @SerialName("required_skills")
    val requiredSkills: List<String> = emptyList(),
    @SerialName("description_text")
    val descriptionText: String = "",
    @SerialName("apply_url")
    val applyUrl: String = "",
    @SerialName("posted_at")
    val postedAt: String? = null,
    @SerialName("is_active")
    val isActive: Boolean? = true
)
