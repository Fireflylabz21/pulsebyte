package com.fireflylabz.pulsebyte.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.auth.Auth

object PulseSupabase {
    val client = createSupabaseClient(
        supabaseUrl = "https://ixjzzcwvkuvlxcypwqkj.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Iml4anp6Y3d2a3V2bHhjcnB3cWtqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDEyMzg2ODksImV4cCI6MjA1NjgxNDY4OX0"
    ) {
        install(Postgrest)
        install(Auth)
    }
}
