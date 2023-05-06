package com.yingda.rxdemo

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.webkit.WebSettings
import androidx.appcompat.app.AppCompatActivity
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.TbsReaderView
import com.yingda.rxdemo.databinding.ActivityMainBinding
import com.yingda.rxtools.binding.viewbind
import com.yingda.rxtools.gsls.GT
import com.yingda.rxtools.log.ViseLog
import com.yingda.rxtools.permissions.OnPermissionCallback
import com.yingda.rxtools.permissions.Permission
import com.yingda.rxtools.permissions.RxPermissions
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Timer
import java.util.TimerTask


class MainActivity : AppCompatActivity() {

    //数据层
    private val viewmodel: MainViewModel by viewModel()

    //绑定视图
    private val mBingding: ActivityMainBinding by viewbind()


    private val timer = Timer()

    private var second = 0

    private lateinit var x5Util: X5Util


    private val tbsReaderTemp =
        Environment.getExternalStorageDirectory().toString() + "/TbsReaderTemp"
    var mTbsReaderView: TbsReaderView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBingding.apply {
            data = viewmodel
            viewmodel.generateTimber()
        }

        mBingding.tvOne.setOnClickListener {
            //发起网络请求
            viewmodel.networkRequest()
        }

        RxPermissions.with(this@MainActivity)
            //.permission(Permission.MANAGE_EXTERNAL_STORAGE)
            //.permission(Permission.READ_EXTERNAL_STORAGE)
            //.permission(Permission.WRITE_EXTERNAL_STORAGE)
            .permission(Permission.READ_MEDIA_AUDIO)
            .permission(Permission.READ_MEDIA_VIDEO)
            .permission(Permission.READ_MEDIA_IMAGES)
            //.permission(Permission.READ_EXTERNAL_STORAGE)
            //.permission(Permission.WRITE_EXTERNAL_STORAGE)
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: List<String?>?, all: Boolean) {

                }

                override fun onDenied(permissions: List<String?>?, never: Boolean) {
                    if (never) {
                        GT.toast(this@MainActivity, "授权成功")
                    } else {
                        GT.toast_time(this@MainActivity, "被永久拒绝授权，请手动授予权限", 5000)
                        RxPermissions.startPermissionActivity(this@MainActivity, permissions)
                    }
                }

            })

        mBingding.webeee.let {
            it.settings.allowContentAccess = true
            it.settings.allowFileAccess = true
            it.settings.textZoom = 100
            it.settings.cacheMode = WebSettings.LOAD_DEFAULT
            it.settings.setAllowFileAccessFromFileURLs(true)
            it.settings.javaScriptEnabled = true
            it.addJavascriptInterface(this, "X5")
            it.webChromeClient = MyChromeClient()
            it.webViewClient = MyWebViewClient()

//            it.loadUrl("debugtbs.qq.com")
//            it.loadUrl("file:android_asset/index.html")
//            it.loadUrl("http://service.spiritsoft.cn/ua.html")
//            it.evaluateJavascript("setTips(\"内核初始化中...\")", null)
        }
        startCheckCore()

        ViseLog.i(QbSdk.getTbsVersion(this))

        var abi: String? = Build.CPU_ABI

        ViseLog.i(abi)


//        //创建默认线程池
//        object : ThreadTaskObject() {
//            override fun run() {
//                //线程执行体
//            }
//        }.start()
//        //创建一个定长线程池定时任务
//        val executorService = ThreadPoolHelp.Builder.schedule(1).scheduleBuilder()
//        executorService.schedule({
//            //发起网络请求
//            viewmodel.networkRequest()
//        }, 1200, TimeUnit.MILLISECONDS)


        //全局作用域启动协程
//        GlobalScope.launch {
//            delay(1000)
//            ViseLog.i("运行")
//
//            Thread.sleep(2000)
//            ViseLog.i("结束")
//        }


    }




    /**
     * 开始检测x5内核
     */
    private fun startCheckCore() {

        // 如果没有安装x5内核则进行安装
        if (QbSdk.getTbsVersion(this) <= 0) {
            // 初始化x5util
            x5Util = X5Util(this@MainActivity, object : LocalInstallListener {
                @SuppressLint("SetTextI18n")
                override fun onSuccess() {
                    timer.cancel()

                    ViseLog.i("内核安装${second}s后成功")
                    runOnUiThread {
                        mBingding.webeee.evaluateJavascript(
                            "setTips(\"内核安装${second}s后成功\")",
                            null
                        )
                    }
                }

                @SuppressLint("SetTextI18n")
                override fun onError(message: String) {
                    ViseLog.e("内核安装${second}s后失败，$message")
                    runOnUiThread {
                        mBingding.webeee.evaluateJavascript(
                            "setTips(\"内核安装${second}s后失败，$message\")",
                            null
                        )
                    }
                    timer.cancel()
                }
            })

            customTimer()

            Thread {
                // 子线程安装内核
                x5Util.startInstallX5()
            }.start()
        } else {
            mBingding.webeee.evaluateJavascript("setTips(\"内核加载成功\")", null)
        }
    }

    /**
     * 定时器，定时将状态更新到html中
     */
    private fun customTimer() {
        timer.schedule(object : TimerTask() {
            @SuppressLint("SetTextI18n")
            override fun run() {
                runOnUiThread {
                    second++
                    mBingding.webeee.evaluateJavascript(
                        "setTips(\"内核初始化中...${second}s\")",
                        null
                    )
                }
            }
        }, 0, 1000)
    }

}