# Keep data classes used with Room
-keep class com.example.countrytracker.data.local.entity.** { *; }
-keep class com.example.countrytracker.domain.model.** { *; }

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Keep Compose classes
-keep class androidx.compose.** { *; }
-keepclassmembers class androidx.compose.** { *; }
