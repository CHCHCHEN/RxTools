package com.yingda.rxtools.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

/**
 * author: chen
 * data: 2023/5/11
 * des: Android 4.3 权限委托实现
 */
@RequiresApi(api = AndroidVersion.ANDROID_4_3)
class PermissionDelegateImplV18 extends PermissionDelegateImplV14 {

    @Override
    public boolean isGrantedPermission(@NonNull Context context, @NonNull String permission) {
        // 检测通知栏监听权限
        if (PermissionUtils.equalsPermission(permission, Permission.BIND_NOTIFICATION_LISTENER_SERVICE)) {
            return NotificationListenerPermissionCompat.isGrantedPermission(context);
        }
        return super.isGrantedPermission(context, permission);
    }

    @Override
    public boolean isPermissionPermanentDenied(@NonNull Activity activity, @NonNull String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.BIND_NOTIFICATION_LISTENER_SERVICE)) {
            return false;
        }
        return super.isPermissionPermanentDenied(activity, permission);
    }

    @Override
    public Intent getPermissionIntent(@NonNull Context context, @NonNull String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.BIND_NOTIFICATION_LISTENER_SERVICE)) {
            return NotificationListenerPermissionCompat.getPermissionIntent(context);
        }
        return super.getPermissionIntent(context, permission);
    }
}
