package com.fireflylabz.taptap.domain.model

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

fun matchesCategory(clusterCat: String, targetCat: String): Boolean {
    val c = clusterCat.lowercase().trim()
    val t = targetCat.lowercase().trim()
    if (c == t || c.contains(t) || t.contains(c)) return true

    return when {
        t.contains("health") && (c.contains("health") || c.contains("wellness")) -> true
        t.contains("lifestyle") && (c.contains("lifestyle") || c.contains("consumer")) -> true
        t.contains("science") && (c.contains("science") || c.contains("tech")) -> true
        t.contains("business") && (c.contains("business") || c.contains("finance") || c.contains("economy")) -> true
        t.contains("politics") && (c.contains("politics") || c.contains("government")) -> true
        t.contains("crime") && (c.contains("crime") || c.contains("safety")) -> true
        t.contains("weather") && (c.contains("weather") || c.contains("disaster")) -> true
        t.contains("entertainment") && (c.contains("entertainment") || c.contains("celebrity")) -> true
        else -> false
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

    val formattedDate: String
        get() {
            val dateStr = createdAtField ?: return "Today"
            return try {
                if (dateStr.length >= 10) {
                    val parts = dateStr.substring(0, 10).split("-")
                    if (parts.size == 3) {
                        val monthNum = parts[1].toIntOrNull() ?: 1
                        val dayNum = parts[2].toIntOrNull() ?: 1
                        val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                        val monthName = months.getOrElse(monthNum - 1) { "Jan" }
                        val dayFormatted = String.format(java.util.Locale.US, "%02d", dayNum)
                        "$monthName-$dayFormatted"
                    } else {
                        dateStr.substring(0, 10)
                    }
                } else dateStr
            } catch (e: Exception) {
                "Today"
            }
        }
}
