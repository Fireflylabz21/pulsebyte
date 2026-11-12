# --- Supabase SDK & Ktor ---
-dontwarn io.github.jan.supabase.**
-keep class io.github.jan.supabase.** { *; }
-keep class io.ktor.** { *; }
-keepattributes Signature, InnerClasses, EnclosingMethod, RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# --- Kotlinx Serialization ---
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
-keepnames @kotlinx.serialization.Serializable class *
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}
-keep class kotlinx.serialization.** { *; }

# --- App Domain & Data Models ---
-keep class com.fireflylabz.taptap.domain.model.** { *; }
-keep class com.fireflylabz.taptap.data.remote.** { *; }
-keep class com.fireflylabz.taptap.ui.** { *; }

# --- Coroutines ---
-keepattributes InnerClasses,Signature,EnclosingMethod
-dontwarn kotlinx.coroutines.internal.MainDispatcherFactory
-dontwarn kotlinx.coroutines.android.AndroidDispatcherFactory
-keep class kotlinx.coroutines.** { *; }
