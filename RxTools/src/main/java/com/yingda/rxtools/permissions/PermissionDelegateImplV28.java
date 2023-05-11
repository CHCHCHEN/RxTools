package com.yingda.rxtools.permissions;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

/**
 * author: chen
 * data: 2023/5/11
 * des: Android 9.0 权限委托实现
 */
@RequiresApi(api = AndroidVersion.ANDROID_9)
class PermissionDelegateImplV28 extends PermissionDelegateImplV26 {

    @Override
    public boolean isGrantedPermission(@NonNull Context context, @NonNull String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.ACCEPT_HANDOVER)) {
            return PermissionUtils.checkSelfPermission(context, permission);
        }
        return super.isGrantedPermission(context, permission);
    }

    @Override
    public boolean isPermissionPermanentDenied(@NonNull Activity activity, @NonNull String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.ACCEPT_HANDOVER)) {
            return !PermissionUtils.checkSelfPermission(activity, permission) &&
                    !PermissionUtils.shouldShowRequestPermissionRationale(activity, permission);
        }
        return super.isPermissionPermanentDenied(activity, permission);
    }
}