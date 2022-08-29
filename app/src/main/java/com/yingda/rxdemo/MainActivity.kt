package com.yingda.rxdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yingda.rxdemo.databinding.ActivityMainBinding
import com.yingda.rxtools.binding.viewbind
import com.yingda.rxtools.gsls.GT
import com.yingda.rxtools.permissions.OnPermissionCallback
import com.yingda.rxtools.permissions.Permission
import com.yingda.rxtools.permissions.RxPermissions
import com.yingda.rxtools.thread.ThreadPoolHelp
import com.yingda.rxtools.thread.ThreadTaskObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    
    //数据层
    private val viewmodel: MainViewModel by viewModel()
    
    //绑定视图
    private val mBingding: ActivityMainBinding by viewbind()
    
    
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
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: List<String?>?, all: Boolean) {
                }
                
                override fun onDenied(permissions: List<String?>?, never: Boolean) {
                    if (never) {
                        GT.toast(this@MainActivity,"授权成功")
                    } else {
                        GT.toast_time(this@MainActivity, "被永久拒绝授权，请手动授予权限", 5000)
                        RxPermissions.startPermissionActivity(this@MainActivity, permissions)
                    }
                }
                
            })
    
        //创建默认线程池
        object : ThreadTaskObject() {
            override fun run() {
                //线程执行体
            }
        }.start()
        //创建一个定长线程池定时任务
        val executorService = ThreadPoolHelp.Builder.schedule(1).scheduleBuilder()
        executorService.schedule({
            //发起网络请求
            viewmodel.networkRequest()
        }, 1200, TimeUnit.MILLISECONDS)
    }
}