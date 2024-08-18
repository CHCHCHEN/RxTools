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

# OAID
-keep class com.yingda.rxtools.rxoaid.uodis.opendevice.aidl.** { *; }
-keep interface com.yingda.rxtools.rxoaid.uodis.opendevice.aidl.** { *; }
-keep class com.yingda.rxtools.rxoaid.asus.msa.SupplementaryDID.** { *; }
-keep interface com.yingda.rxtools.rxoaid.asus.msa.SupplementaryDID.** { *; }
-keep class com.yingda.rxtools.rxoaid.bun.lib.** { *; }
-keep interface com.yingda.rxtools.rxoaid.bun.lib.** { *; }
-keep class com.yingda.rxtools.rxoaid.heytap.openid.** { *; }
-keep interface com.yingda.rxtools.rxoaid.heytap.openid.** { *; }
-keep class com.yingda.rxtools.rxoaid.samsung.android.deviceidservice.** { *; }
-keep interface com.yingda.rxtools.rxoaid.samsung.android.deviceidservice.** { *; }
-keep class com.yingda.rxtools.rxoaid.zui.deviceidservice.** { *; }
-keep interface com.yingda.rxtools.rxoaid.zui.deviceidservice.** { *; }
-keep class com.yingda.rxtools.rxoaid.coolpad.deviceidsupport.** { *; }
-keep interface com.yingda.rxtools.rxoaid.coolpad.deviceidsupport.** { *; }
-keep class com.yingda.rxtools.rxoaid.android.creator.** { *; }
-keep interface com.yingda.rxtools.rxoaid.android.creator.** { *; }
-keep class com.yingda.rxtools.rxoaid.google.android.gms.ads.identifier.internal.** { *; }
-keep interface com.yingda.rxtools.rxoaid.google.android.gms.ads.identifier.internal.* { *; }
-keep class com.yingda.rxtools.rxoaid.oplus.stdid.** {*; }
-keep interface com.yingda.rxtools.rxoaid.oplus.stdid.** {*; }
-keep class com.huawei.hms.ads.** {*; }
-keep interface com.huawei.hms.ads.** {*; }
-keep class com.hihonor.ads.** {*; }
-keep interface com.hihonor.ads.** {*; }
-keep class com.yingda.rxtools.rxoaid.qiku.id.** { *; }
-keep interface com.yingda.rxtools.rxoaid.qiku.id.** { *; }

#Binding
-keepclassmembers class ** implements androidx.viewbinding.ViewBinding {
    public static ** bind(***);
    public static ** inflate(***);
}

-keep class com.yingda.rxtools.** { *; }

#http
-keep public class * extends com.yingda.rxtools.adapter.viewholder.BaseViewHolder
-keepclassmembers  class **$** extends com.yingda.rxtools.adapter.viewholder.BaseViewHolder {
     <init>(...);
}

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