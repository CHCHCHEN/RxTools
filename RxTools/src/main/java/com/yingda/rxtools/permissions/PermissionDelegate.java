package com.yingda.rxtools.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

/**
 * author: chen
 * data: 2023/5/11
 * des: 权限委托接口
 */
public interface PermissionDelegate {

    /**
     * 判断某个权限是否授予了
     */
    boolean isGrantedPermission(@NonNull Context context, @NonNull String permission);

    /**
     * 判断某个权限是否永久拒绝了
     */
    boolean isPermissionPermanentDenied(@NonNull Activity activity, @NonNull String permission);

    /**
     * 获取权限设置页的意图
     */
    Intent getPermissionIntent(@NonNull Context context, @NonNull String permission);
}