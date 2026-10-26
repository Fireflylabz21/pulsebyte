package com.fireflylabz.pulsebyte.domain.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

object SummaryBulletsDeserializer : KSerializer<List<String>> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("SummaryBullets", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): List<String> {
        val jsonInput = decoder as? JsonDecoder ?: return emptyList()
        val element = jsonInput.decodeJsonElement()
        return when (element) {
            is JsonArray -> element.map { it.jsonPrimitive.content }
            is JsonPrimitive -> {
                val content = element.content
                if (content.startsWith("{") && content.endsWith("}")) {
                    content.removeSurrounding("{", "}")
                        .split(",")
                        .map { it.trim().removeSurrounding("\"") }
                } else {
                    listOf(content)
                }
            }
            else -> emptyList()
        }
    }

    override fun serialize(encoder: Encoder, value: List<String>) {
        val jsonOutput = encoder as? JsonEncoder ?: return
        jsonOutput.encodeJsonElement(JsonArray(value.map { JsonPrimitive(it) }))
    }
}

@Serializable
data class NewsCluster(
    @SerialName("id") val id: String = "",
    @SerialName("objective_title") val objectiveTitle: String? = null,
    @Serializable(with = SummaryBulletsDeserializer::class)
    @SerialName("ai_summary_bullets") val aiSummaryBullets: List<String>? = null,
    @SerialName("category") val categoryField: String? = null,
    @SerialName("created_at") val createdAtField: String? = null,
    @SerialName("updated_at") val updatedAtField: String? = null,
    @SerialName("is_breaking") val isBreakingField: Boolean? = null,
    @SerialName("reading_time_minutes") val readingTimeMinutesField: Int? = null,
    @SerialName("audio_summary_url") val audioSummaryUrlField: String? = null,
    @SerialName("source_url") val sourceUrlField: String? = null,
    @SerialName("source_names") val sourceNamesField: String? = null
) {
    val title: String get() = objectiveTitle?.takeIf { it.isNotBlank() } ?: "Untitled Story"
    val category: String get() = categoryField?.takeIf { it.isNotBlank() } ?: "Politics & Government"
    val summaryBullets: List<String> get() = aiSummaryBullets?.filter { it.isNotBlank() } ?: emptyList()
    val createdAt: String? get() = createdAtField
    val updatedAt: String? get() = updatedAtField
    val isBreaking: Boolean get() = isBreakingField == true
    val readingTimeMinutes: Int get() = readingTimeMinutesField ?: 1
    val audioSummaryUrl: String? get() = audioSummaryUrlField
    val sourceUrl: String? get() = sourceUrlField
    val sourceNames: String? get() = sourceNamesField

    val displayTitle: String get() = title
    val displayCategory: String get() = category
    val displayBullets: List<String> get() = summaryBullets
    val publishedTimeAgo: String get() = "Just now"
    val sourcesCount: Int get() = 3
    val imageUrl: String? get() = null
}
