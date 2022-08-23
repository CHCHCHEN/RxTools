package com.yingda.rxtools.wechat

import android.annotation.SuppressLint
import android.content.Context

/**
 * author: chen
 * data: 2021-8-30
 * des: 微信SDK初始化
 */
class WeChatHelper private constructor(context: Context) : WeChatBaseHelper(context) {


    companion object {
        var IS_LOGGABLE: Boolean = false
        
        @Volatile
        @SuppressLint("StaticFieldLeak")
        private var instance: WeChatHelper? = null

        @JvmStatic
        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: WeChatHelper(context).also { instance = it }
        }

    }

    fun init(isLoggable: Boolean): Boolean {

        IS_LOGGABLE = isLoggable

        // 将应用的appId注册到微信
        api.registerApp(mWeChatAppId)
        //val isInitWeChat = api.registerApp(mWeChatAppId)
        //Logger.d("isInitWeChat = $isInitWeChat  mWeChatAppId = $mWeChatAppId")

//            //建议动态监听微信启动广播进行注册到微信
//            context.registerReceiver(object : BroadcastReceiver() {
//                override fun onReceive(context: Context, intent: Intent) {
//                    Log.i("WeChatLoginLibrary", "动态注册到微信 : ${this@apply}")
//                    // 将该app注册到微信
//                    wxApi.registerApp(this@apply)
//                }
//            }, IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP))

        return true
    }

}