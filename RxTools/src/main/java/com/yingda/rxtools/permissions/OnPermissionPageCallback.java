package com.yingda.rxtools.permissions;

/**
 * author: chen
 * data: 2023/5/11
 * des: 权限设置页结果回调接口
*/
public interface OnPermissionPageCallback {

    /**
     * 权限已经授予
     */
    void onGranted();

    /**
     * 权限已经拒绝
     */
    default void onDenied() {}
}