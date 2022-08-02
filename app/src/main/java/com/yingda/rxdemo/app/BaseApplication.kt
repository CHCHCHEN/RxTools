package com.yingda.rxdemo.app

import android.app.Application
import android.content.Context
import android.util.Log
import com.yingda.rxdemo.BuildConfig
import com.yingda.rxtools.gsls.GT.Hibernate
import com.yingda.rxtools.log.ViseLog
import com.yingda.rxtools.log.inner.LogcatTree
import com.yingda.rxtools.wechat.WeChatHelper.Companion.getInstance
import org.koin.android.ext.koin.androidContext
import org.koin.android.logger.AndroidLogger
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.logger.Level


/**
 * Author: CHEN
 * date:2022-3-4
 * des:
 */
class BaseApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        app = this@BaseApplication

        //初始化viewModel
        startKoin {
            AndroidLogger(Level.DEBUG)
            androidContext(this@BaseApplication)
            loadKoinModules(appModels)
        }

        //创建数据库
        hibernate = Hibernate(this@BaseApplication)                  //实例化 Hibernate
            .init_1_SqlName("RxTools")                              //设置数据库名称
            .init_2_SqlVersion(1)                               //设置数据库版本号
            .init_3_SqlTable("com.yingda.rxdemo.sqlbeen")      //加载表
            .init_4_Sql()                                               //执行数据库操作

       
        //工具日志输出
        ViseLog.getLogConfig()
            .configShowBorders(true)                         //是否排版显示
            .configTagPrefix("ViseLog")                           //设置标签前缀
            .configAllowLog(true)                               //是否输出日志
            .configFormatTag("%d{HH:mm:ss:SSS} %t %c{-5}")    //个性化设置标签，默认显示包名
            .configLevel(Log.VERBOSE)                                   //设置日志最小输出级别，默认Log.VERBOSE
        ViseLog.plant(LogcatTree())                                     //添加打印日志信息到Logcat的树

       

        //初始化微信SDK
        getInstance(this).init(BuildConfig.DEBUG)
        
    }


    /**
     * Application静态方法
     */
    companion object {

        private var app: Application? = null
        private var hibernate: Hibernate? = null


        //全局数据库sql对象
        fun getHibernate(): Hibernate {
            return hibernate!!
        }

        /**
         * 获取Application的Context
         */
        fun getAppContext(): Context? {
            return if (app == null) null else app!!.applicationContext
        }
    }

}