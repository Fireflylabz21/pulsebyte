package com.fireflylabz.pulsebyte.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json

object PulseSupabase {
    val client = createSupabaseClient(
        supabaseUrl = "https://ixjzzcwvkuvlxcypwqkj.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Iml4anp6Y3d2a3V2bHhjcnB3cWtqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTI4Mjk3NzYsImV4cCI6MjEwODQwNTc3Nn0.mNuxLbJH2SdB1BO9r1xPdD_uhagByEaEZkiQ2bylUfI"
    ) {
        defaultSerializer = KotlinXSerializer(
            Json {
                ignoreUnknownKeys = true
                isLenient = true
                coerceInputValues = true
            }
        )
        install(Postgrest)
        install(Auth)
    }
}
