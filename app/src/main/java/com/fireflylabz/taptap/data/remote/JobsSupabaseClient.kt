package com.fireflylabz.taptap.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.realtime.Realtime

object JobsSupabase {
    val client = createSupabaseClient(
        supabaseUrl = "https://kgdiydsokitpmpzwzwje.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImtnZGl5ZHNva2l0cG1wend6d2plIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTE2ODYwMzYsImV4cCI6MjEwNzI2MjAzNn0.ShOKWnAlJIvBS-kqZEQSfHkn5XhJ0kYkzdzKJ_8-S4Y"
    ) {
        install(Postgrest)
        install(Auth)
        install(Realtime)
    }
}
