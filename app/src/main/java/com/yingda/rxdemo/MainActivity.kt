package com.yingda.rxdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yingda.rxdemo.databinding.ActivityMainBinding
import com.yingda.rxtools.gsls.GT
import com.yingda.rxtools.binding.viewbind
import com.yingda.rxtools.log.ViseLog

class MainActivity : AppCompatActivity() {
    
    
    //绑定视图
    private val mBingding: ActivityMainBinding by viewbind()
    
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBingding.apply {
            ViseLog.i(mBingding)
        }
    }
}