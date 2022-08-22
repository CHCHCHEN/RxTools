package com.yingda.rxtools.permissions;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;

/**
 * author: chen
 * data: 2022/8/18
 * des: Android 12 权限委托实现
 */
@RequiresApi(api = Build.VERSION_CODES.S)
class PermissionDelegateImplV31 extends PermissionDelegateImplV30 {

    @Override
    public boolean isGrantedPermission(Context context, String permission) {
        // 检测闹钟权限
        if (PermissionUtils.equalsPermission(permission, Permission.SCHEDULE_EXACT_ALARM)) {
            return isGrantedAlarmPermission(context);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.BLUETOOTH_SCAN) ||
                PermissionUtils.equalsPermission(permission, Permission.BLUETOOTH_CONNECT) ||
                PermissionUtils.equalsPermission(permission, Permission.BLUETOOTH_ADVERTISE)) {
            return PermissionUtils.checkSelfPermission(context, permission);
        }
        return super.isGrantedPermission(context, permission);
    }

    @Override
    public boolean isPermissionPermanentDenied(Activity activity, String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.SCHEDULE_EXACT_ALARM)) {
            return false;
        }

        if (PermissionUtils.equalsPermission(permission, Permission.BLUETOOTH_SCAN) ||
                PermissionUtils.equalsPermission(permission, Permission.BLUETOOTH_CONNECT) ||
                PermissionUtils.equalsPermission(permission, Permission.BLUETOOTH_ADVERTISE)) {
            return !PermissionUtils.checkSelfPermission(activity, permission) &&
                    !PermissionUtils.shouldShowRequestPermissionRationale(activity, permission);
        }

        if (activity.getApplicationInfo().targetSdkVersion >= AndroidVersion.Companion.getANDROID_12() &&
                PermissionUtils.equalsPermission(permission, Permission.ACCESS_BACKGROUND_LOCATION)) {
            if (!PermissionUtils.checkSelfPermission(activity, Permission.ACCESS_FINE_LOCATION) &&
                    !PermissionUtils.checkSelfPermission(activity, Permission.ACCESS_COARSE_LOCATION)) {
                return !PermissionUtils.shouldShowRequestPermissionRationale(activity, Permission.ACCESS_FINE_LOCATION) &&
                        !PermissionUtils.shouldShowRequestPermissionRationale(activity, Permission.ACCESS_COARSE_LOCATION);
            }

            return !PermissionUtils.checkSelfPermission(activity, permission) &&
                    !PermissionUtils.shouldShowRequestPermissionRationale(activity, permission);
        }
        return super.isPermissionPermanentDenied(activity, permission);
    }

    @Override
    public Intent getPermissionIntent(Context context, String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.SCHEDULE_EXACT_ALARM)) {
            return getAlarmPermissionIntent(context);
        }

        return super.getPermissionIntent(context, permission);
    }

    /**
     * 是否有闹钟权限
     */
    private static boolean isGrantedAlarmPermission(Context context) {
        return context.getSystemService(AlarmManager.class).canScheduleExactAlarms();
    }

    /**
     * 获取闹钟权限设置界面意图
     */
    private static Intent getAlarmPermissionIntent(Context context) {
        Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
        intent.setData(PermissionUtils.getPackageNameUri(context));
        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent = PermissionUtils.getApplicationDetailsIntent(context);
        }
        return intent;
    }
}