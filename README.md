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
  implementation 'com.github.CHCHCHEN:RxTools:1.3.8'
}
```

RxTools 最新版本 [![](https://jitpack.io/v/CHCHCHEN/RxTools.svg)](https://jitpack.io/#CHCHCHEN/RxTools)

## Binding

**ViewBinding 中的使用**

```
val binding: ActivityViewBindBinding by viewbind()
```

**DataBinding 中的使用**

```
val binding: ActivityDataBindBinding by databind(R.layout.activity_data_bind)
或者
val binding: ActivityDataBindBinding by databind()
```

在 `Ativity` 、 `Fragment` 、 `Dialog` 、 `Adapter` 、 `include` 、 `merge` 、 `ViewStub` 、 `Navigation`  、 `ViewGroup` 、 数据双向绑定 等等场景中如何使用。

## 使用

在自定义 ViewGroup 中使用 DataBinding 和 ViewBinding，

* ViewBinding 两种用法
    * 当根布局是非 merge 标签，使用此方法进行初始化 `val binding: LayoutViewCustomBinding by viewbind()`
    * 当根布局为 merge 标签，使用此方法进行初始化 `val binding: LayoutViewCustomBinding by viewbind(this)`

* DataBinding 的使用

  ```
  val binding: LayoutViewCustomDataBinding by databind(R.layout.layout_view_custom_data)
  ```  


## 添加 `WECHAT_APP_ID` 到 app build.gradle

``` gradle
android {
    ……
    defaultConfig {
        ……
        manifestPlaceholders = [
                WECHAT_APP_ID: "微信appid"
                WECHAT_APP_SECRET: "微信app_secret"
        ]
        ……
    }
    ……
}
```

## 微信

``` kotlin
//初始化
WeChatClient.init(applicationContext, BuildConfig.DEBUG)
```

* 分享Scene

Timeline 朋友圈 Favorite 收藏夹 Session 联系人

* 分享文字

``` kotlin
WeChatClient.shareText(`分享的文字`, `Scene`, `OnWeChatShareListener`)
```

* 分享图片

``` kotlin
WeChatClient.shareImage(`Bitmap`, `Scene`, `OnWeChatShareListener`)
```

* 分享音乐

``` kotlin
WeChatClient.shareMusic(`Bitmap`, `Scene`, `音乐链接`, `分享音乐名称`, `分享音乐描述`, `OnWeChatShareListener`)
```

* 分享视频

``` kotlin
WeChatClient.shareVideo(`Bitmap`, `Scene`, `视频链接`, `分享视频名称`, `分享视频描述`, `OnWeChatShareListener`)
```

* 分享网址

``` kotlin
WeChatClient.shareWebPage(`Bitmap`, `Scene`, `网址链接`, `分享网址名称`, `分享网址描述`, `OnWeChatShareListener`)
```

* 授权登录

``` kotlin
WeChatClient.authLogin(`OnWeChatAuthLoginListener`)
```


* 支付

``` kotlin
WeChatClient.payment(`IPaymentParams` ,`OnWeChatPaymentListener`)
```

## 权限

#### 分区存储

*适配Android 10 分区存储特性
``` xml
<manifest
    </application>
        <!-- 表示当前项目已经适配了分区存储特性 -->
        <meta-data
            android:name="ScopedStorage"
            android:value="true" />
    </application>
</manifest>
```
*使用

``` kotlin
RxPermissions.with(this@MainActivity)
            //.permission(Permission.MANAGE_EXTERNAL_STORAGE)
            //.permission(Permission.READ_EXTERNAL_STORAGE)
            //.permission(Permission.WRITE_EXTERNAL_STORAGE)
            .permission(Permission.READ_MEDIA_AUDIO)
            .permission(Permission.READ_MEDIA_VIDEO)
            .permission(Permission.READ_MEDIA_IMAGES)
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: List<String?>?, all: Boolean) {
                }
                
                override fun onDenied(permissions: List<String?>?, never: Boolean) {
                    if (never) {
                    
                    } else {
                        GT.toast_time(this@MainActivity, "被永久拒绝授权，请手动授予权限", 5000)
                        RxPermissions.startPermissionActivity(this@MainActivity, permissions)
                    }
                }
                
            })    
```

## 线程池

``` kotlin
//创建默认线程池
object : ThreadTaskObject() {
override fun run() {
//线程执行体
}
}.start()
//创建一个定长线程池定时任务
val executorService = ThreadPoolHelp.Builder.schedule(1).scheduleBuilder()
executorService.schedule({
//线程执行体
}, 1200, TimeUnit.MILLISECONDS)
```


## 混淆

```
-keep class com.yingda.rxtools.** { *.; }
-keep class com.tencent.mm.opensdk.** { *; }
-keep class com.tencent.wxop.** { *; }
-keep class com.tencent.mm.sdk.** { *; }

```
