# RxTools
# 如何使用它


## Step 1.先在 build.gradle(Project:XXXX) 的 repositories 添加:

```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

## Step 2.然后在 build.gradle(Module:app) 的 dependencies 添加:

```gradle
dependencies {
  //基础工具库
  implementation 'com.github.CHCHCHEN:RxTools:1.1.7'
}
```
