package com.yingda.rxtools.permissions

/**
 * author: chen
 * data: 2022/8/18
 * des: 权限设置页结果回调接口
 */
interface OnPermissionPageCallback {
    /**
     * 权限已经授予
     */
    fun onGranted()
    
    /**
     * 权限已经拒绝
     */
    fun onDenied()
}