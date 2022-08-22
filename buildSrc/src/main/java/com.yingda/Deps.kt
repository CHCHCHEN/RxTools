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
    
    //Android
    val material = "1.4.0"
    
    val junit = "4.13.2"
    val gson = "2.8.5"
    val rxkotlin = "2.2.0"
    val rxandroid = "2.0.2"
    val rxadapter = "2.9.0"
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
    }
    
    object Android {
        val material = "com.google.android.material:material:${Versions.material}"
    }
    
    
    //other
    
    val junit = "junit:junit:${Versions.junit}"
    
    val gson = "com.google.code.gson:gson:${Versions.gson}"
    val rxkotlin = "io.reactivex.rxjava2:rxkotlin:${Versions.rxkotlin}"
    val rxandroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxandroid}"
    val rxadapter = "com.squareup.retrofit2:adapter-rxjava2:${Versions.rxadapter}"
    val wechat = "com.tencent.mm.opensdk:wechat-sdk-android-without-mta:+"
    
}