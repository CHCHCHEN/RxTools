package com.yingda.rxdemo

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.yingda.rxtools.http.RxHttp
import com.yingda.rxtools.http.callback.SimpleCallBack
import com.yingda.rxtools.http.exception.ApiException
import com.yingda.rxtools.log.ViseLog
import java.util.*

/**
 * author: chen
 * data: 2022/8/2
 * des:
 */
class MainViewModel : ViewModel() {
    
    val inputString = ObservableField<String>()
    
    init {
        ViseLog.i("MainViewModel")
    }
    
    /**
     * 通过 android:text="@={data.generateTimber}" 进行双向绑定
     */
    fun generateTimber() {
        val timer = Timer()
        timer.schedule(
            object : TimerTask() {
                override fun run() {
                    inputString.set(" 双向绑定示例 \n 时间: " + System.currentTimeMillis())
                }
            },
            0, 500
        )
    }
    
    fun networkRequest() {
        RxHttp.post("api/topbaidu/")
            .accessToken(false)
            .timeStamp(true)
            .execute(object : SimpleCallBack<String?>() {
                override fun onError(e: ApiException?) {
                    ViseLog.e("请求失败${e.toString()}")
                }
                
                override fun onSuccess(t: String?) {
                    ViseLog.i("请求成功$t")
                }
            })
    }
    
}