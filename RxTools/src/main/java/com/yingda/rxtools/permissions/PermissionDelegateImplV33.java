package com.yingda.rxtools.permissions;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

/**
 * author: chen
 * data: 2022/8/18
 * des:  Android 13 权限委托实现
 */
@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
class PermissionDelegateImplV33 extends PermissionDelegateImplV31 {

    @Override
    public boolean isGrantedPermission(Context context, String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.BODY_SENSORS_BACKGROUND)) {
            // 有后台传感器权限的前提条件是要有前台的传感器权限
            return PermissionUtils.checkSelfPermission(context, Permission.BODY_SENSORS) &&
                    PermissionUtils.checkSelfPermission(context, Permission.BODY_SENSORS_BACKGROUND);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.POST_NOTIFICATIONS) ||
                PermissionUtils.equalsPermission(permission, Permission.NEARBY_WIFI_DEVICES) ||
                PermissionUtils.equalsPermission(permission, Permission.READ_MEDIA_IMAGES) ||
                PermissionUtils.equalsPermission(permission, Permission.READ_MEDIA_VIDEO) ||
                PermissionUtils.equalsPermission(permission, Permission.READ_MEDIA_AUDIO)) {
            return PermissionUtils.checkSelfPermission(context, permission);
        }
        return super.isGrantedPermission(context, permission);
    }

    @Override
    public boolean isPermissionPermanentDenied(Activity activity, String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.BODY_SENSORS_BACKGROUND)) {
            if (!PermissionUtils.checkSelfPermission(activity, Permission.BODY_SENSORS)) {
                return !PermissionUtils.shouldShowRequestPermissionRationale(activity, Permission.BODY_SENSORS);
            }
            return !PermissionUtils.checkSelfPermission(activity, permission) &&
                    !PermissionUtils.shouldShowRequestPermissionRationale(activity, permission);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.POST_NOTIFICATIONS) ||
                PermissionUtils.equalsPermission(permission, Permission.NEARBY_WIFI_DEVICES) ||
                PermissionUtils.equalsPermission(permission, Permission.READ_MEDIA_IMAGES) ||
                PermissionUtils.equalsPermission(permission, Permission.READ_MEDIA_VIDEO) ||
                PermissionUtils.equalsPermission(permission, Permission.READ_MEDIA_AUDIO)) {
            return !PermissionUtils.checkSelfPermission(activity, permission) &&
                    !PermissionUtils.shouldShowRequestPermissionRationale(activity, permission);
        }
        return super.isPermissionPermanentDenied(activity, permission);
    }
}