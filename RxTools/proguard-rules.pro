# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#Binding
-keepclassmembers class ** implements androidx.viewbinding.ViewBinding {
    public static ** bind(***);
    public static ** inflate(***);
}

-keep class com.yingda.rxtools.** { *.; }

#GT
-dontwarn com.yingda.rxtools.gsls.**
-keep public class com.yingda.rxtools.gsls.GT { *; }
-keep public class * extends com.yingda.rxtools.gsls.GT { *; }
-keep class com.yingda.rxtools.gsls.GT$* {*;}

#微信
-dontwarn com.yingda.rxtools.wechat.**
-keep class com.yingda.rxtools.wechat.** { *; }

# Gson
-keep class com.google.gson.stream.** { *; }
-keepattributes EnclosingMethod
-keep class org.xz_sale.entity.**{*;}
-keep class com.google.gson.** {*;}
-keep class com.google.**{*;}
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }