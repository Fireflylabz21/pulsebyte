# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles directive in build.gradle.kts.

# --- Kotlinx Serialization ---
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
-keepnames @kotlinx.serialization.Serializable class *
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}

# --- Ktor Client ---
-dontwarn io.ktor.**
-keep class io.ktor.** { *; }

# --- Supabase SDK ---
-dontwarn io.github.jan.supabase.**
-keep class io.github.jan.supabase.** { *; }

# --- App Domain & Data Models ---
-keep class com.fireflylabz.taptap.domain.model.** { *; }
-keep class com.fireflylabz.taptap.data.remote.** { *; }
-keep class com.fireflylabz.taptap.ui.finance.** { *; }

# --- Coroutines ---
-keepattributes InnerClasses,Signature,EnclosingMethod
-dontwarn kotlinx.coroutines.internal.MainDispatcherFactory
-dontwarn kotlinx.coroutines.android.AndroidDispatcherFactory
-keep class kotlinx.coroutines.** { *; }
