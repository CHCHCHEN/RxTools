package com.yingda

import org.gradle.api.JavaVersion

/**
 * author: chen
 * data: 2022/8/22
 * des: 配置Android开发版本
 */

object Versions {
    
    val applicationId = "com.yingda.rxdemo"
    val compileSdkVersion = 33
    val minSdkVersion = 21
    val targetSdkVersion = 33
    val libversionCode = 119
    val libversionName = "1.1.9"
    
    //AndroidX
    val coreKtx = "1.7.0"
    val appcompat = "1.3.0"
    val constraintlayout = "2.0.4"
    val junitx = "1.1.3"
    val espresso = "3.4.0"
    val viewmodel = "2.2.1"
    val recyclerview = "1.2.1"
    val annotation = "1.2.0"
    
    //Android
    val material = "1.4.0"
    
    val junit = "4.13.2"
    val gson = "2.8.5"
    val rxkotlin = "2.2.0"
   val rxjava = "2.2.10"
    val rxandroid = "2.1.1"
    val rxadapter = "2.9.0"
    
    //lib
    val zip4j = "1.3.2"
    val greendao = "3.2.2"
    val exifinterface = "1.3.0"
    val guava = "27.1-jre"
    val commons = "commons-lang3:3.5"
    val jodatime = "2.9.9"
    
    val flexbox = "0.3.2"
    val systembartint = "1.0.3"
}

object common {
    val jdk = JavaVersion.VERSION_1_8
}

object Deps {
    
    object AndroidX {
        val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
        val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
        val constraintlayout =
            "androidx.constraintlayout:constraintlayout:${Versions.constraintlayout}"
        
        val junitx = "androidx.test.ext:junit:${Versions.junitx}"
        val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"
        
        val viewmodel = "org.koin:koin-androidx-viewmodel:${Versions.viewmodel}"
        
        val recyclerview = "androidx.recyclerview:recyclerview:${Versions.recyclerview}"
        val annotation = "androidx.annotation:annotation:1.2.0"
        
        
    }
    
    object Android {
        val material = "com.google.android.material:material:${Versions.material}"
    }
    
    
    //other
    
    val junit = "junit:junit:${Versions.junit}"
    
    val gson = "com.google.code.gson:gson:${Versions.gson}"
    val rxjava = "io.reactivex.rxjava2:rxjava:${Versions.rxjava}"
    val rxkotlin = "io.reactivex.rxjava2:rxkotlin:${Versions.rxkotlin}"
    val rxandroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxandroid}"
    val rxadapter = "com.squareup.retrofit2:adapter-rxjava2:${Versions.rxadapter}"
    val rxretrofit= "com.squareup.retrofit2:retrofit:2.5.0"
    val rxconverter= "com.squareup.retrofit2:converter-gson:2.5.0"
    val wechat = "com.tencent.mm.opensdk:wechat-sdk-android-without-mta:+"
    
    //lib
    val zip4j = "net.lingala.zip4j:zip4j:${Versions.zip4j}"
    
    val greendao = "org.greenrobot:greendao:${Versions.greendao}"
    val exifinterface = "androidx.exifinterface:exifinterface:${Versions.exifinterface}"
    val guava = "com.google.guava:guava:${Versions.guava}"
    
    
    val commons = "org.apache.commons:${Versions.commons}"
    val jodatime = "joda-time:joda-time:${Versions.jodatime}"
    
    //流式布局
    val flexbox = "com.google.android:flexbox:${Versions.flexbox}"
    
    //沉浸式
    val systembartint =
        "com.readystatesoftware.systembartint:systembartint:${Versions.systembartint}"
    
    val disklrucache= "com.jakewharton:disklrucache:2.0.2"
    
    val okhttp3 = "com.squareup.okhttp3:okhttp:3.12.2"
    val okhttp3logging = "com.squareup.okhttp3:logging-interceptor:3.12.2"
}