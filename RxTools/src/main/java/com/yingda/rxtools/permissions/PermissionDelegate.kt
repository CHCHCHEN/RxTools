package com.yingda.rxtools.permissions

import android.app.Activity
import android.content.Context
import android.content.Intent

/**
 * author: chen
 * data: 2022/8/18
 * des: 权限委托接口
 */
interface PermissionDelegate {
    
    /**
     * 判断某个权限是否授予了
     */
    fun isGrantedPermission(context: Context?, permission: String?): Boolean
    
    /**
     * 判断某个权限是否永久拒绝了
     */
    fun isPermissionPermanentDenied(activity: Activity?, permission: String?): Boolean
    
    /**
     * 获取权限设置页的意图
     */
    fun getPermissionIntent(context: Context?, permission: String?): Intent?
}