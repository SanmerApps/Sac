-verbose
-dontpreverify
-optimizationpasses 5
-dontskipnonpubliclibraryclasses

-dontwarn org.conscrypt.**
-dontwarn kotlinx.serialization.**
-dontwarn java.awt.**
-dontwarn org.slf4j.**

# Keep DataStore fields
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite* {
   <fields>;
}

# Keep Skiko
-keep class kotlin.jvm.functions.Function0 { *; }
-keep class org.jetbrains.skia.** { *; }

# Keep Native library
-keep class dev.sanmer.sac.io.SacHeader { *; }
-keepclassmembers class dev.sanmer.sac.io.Sac {
    native <methods>;
}

-repackageclasses dev.sanmer.sac