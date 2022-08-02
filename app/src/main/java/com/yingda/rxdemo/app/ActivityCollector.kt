package com.yingda.rxdemo.app

import android.app.Activity

/**
 * author: chen
 * data: 8/2/21
 * des: 封装Activity相关工具类
 */

object ActivityCollector {

    private val activities = ArrayList<Activity>()

    /**
     * 添加activity
     */
    fun addActivity(activity: Activity) {
        activities.add(activity)
    }

    /**
     * 移除activity
     */
    fun removeActivity(activity: Activity) {
        activities.remove(activity)
    }

    /**
     * 结束当前程序
     */
    fun finishAll() {
        for (activity in activities) {
            if (!activity.isFinishing) {
                activity.finish()
            }
        }
        activities.clear()
        //杀掉当前进程的代码
        //android.os.Process.killProcess(android.os.Process.myPid())
    }
}