package com.yingda.rxdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yingda.rxdemo.databinding.ActivityMainBinding
import com.yingda.rxtools.binding.viewbind
import com.yingda.rxtools.log.ViseLog
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    
    //数据层
    private val viewmodel: MainViewModel by viewModel()
    //绑定视图
    private val mBingding: ActivityMainBinding by viewbind()
    
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        mBingding.apply {
            ViseLog.i(123)
            viewmodel.apply {
            }
        }
    
        mBingding.tvOne.text = "ViewBingding"
    }
}