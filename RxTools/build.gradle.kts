import com.yingda.*

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = Versions.compileSdkVersion
    
    defaultConfig {
        minSdk = Versions.minSdkVersion
        targetSdk = Versions.targetSdkVersion
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    
    compileOnly(Deps.AndroidX.coreKtx)
    compileOnly(Deps.AndroidX.appcompat)
    compileOnly(Deps.Android.material)
    
    compileOnly(Deps.junit)
    compileOnly(Deps.AndroidX.junitx)
    compileOnly(Deps.AndroidX.espresso)
    
    
    //compileOnly(Deps.gson)
    compileOnly(Deps.rxkotlin)
    compileOnly(Deps.rxandroid)
    compileOnly(Deps.rxadapter)
    compileOnly(Deps.wechat)
    
    //recyclerview包
    compileOnly(Deps.AndroidX.recyclerview)
    //compileOnly(Deps.AndroidX.annotation)
    compileOnly(Deps.AndroidX.constraintlayout)
    //流式布局
    compileOnly(Deps.flexbox)
    //沉浸式
    compileOnly(Deps.systembartint)
    
    
    //压缩与加密
    compileOnly(Deps.zip4j)
    
    compileOnly(Deps.greendao)
    compileOnly(Deps.exifinterface)
    compileOnly(Deps.guava)
    
    
    compileOnly(Deps.commons)
    compileOnly(Deps.jodatime)
    
    //http
    compileOnly(Deps.rxjava)
    compileOnly(Deps.rxretrofit)
    compileOnly(Deps.rxconverter)
    compileOnly(Deps.disklrucache)
    compileOnly(Deps.okhttp3)
    compileOnly(Deps.okhttp3logging)
}