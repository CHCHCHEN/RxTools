import com.yingda.*

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    compileSdk = Versions.compileSdkVersion
    
    defaultConfig {
        applicationId = Versions.applicationId
        minSdk = Versions.minSdkVersion
        targetSdk = Versions.targetSdkVersion
        versionCode = Versions.libversionCode
        versionName = Versions.libversionName
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    
    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
    
    //开启viewBinding和dataBinding
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
    
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
        }
    }
    
    compileOptions {
        sourceCompatibility = common.jdk
        targetCompatibility = common.jdk
    }
    
    kotlinOptions {
        jvmTarget = common.jdk.toString()
    }
}

dependencies {
    
    implementation(project(":RxTools"))
    
    implementation(Deps.AndroidX.coreKtx)
    implementation(Deps.AndroidX.appcompat)
    implementation(Deps.Android.material)
    implementation(Deps.AndroidX.constraintlayout)
    
    testImplementation(Deps.junit)
    androidTestImplementation(Deps.AndroidX.junitx)
    androidTestImplementation(Deps.AndroidX.espresso)
    
    //kotlin
    implementation(Deps.AndroidX.viewmodel)
    
    implementation(Deps.gson)
    implementation(Deps.rxkotlin)
    implementation(Deps.rxandroid)
    implementation(Deps.rxadapter)
    implementation(Deps.wechat)
    
    implementation(Deps.disklrucache)
 
}