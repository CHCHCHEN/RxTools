package com.yingda.rxtools.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import androidx.annotation.RequiresApi;

/**
 * author: chen
 * data: 2022/8/18
 * des: Android 11 权限委托实现
 */
@RequiresApi(api = Build.VERSION_CODES.R)
class PermissionDelegateImplV30 extends PermissionDelegateImplV29 {

    @Override
    public boolean isGrantedPermission(Context context, String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.MANAGE_EXTERNAL_STORAGE)) {
            return isGrantedManageStoragePermission();
        }
        return super.isGrantedPermission(context, permission);
    }

    @Override
    public boolean isPermissionPermanentDenied(Activity activity, String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.MANAGE_EXTERNAL_STORAGE)) {
            return false;
        }
        return super.isPermissionPermanentDenied(activity, permission);
    }

    @Override
    public Intent getPermissionIntent(Context context, String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.MANAGE_EXTERNAL_STORAGE)) {
            return getManageStoragePermissionIntent(context);
        }
        return super.getPermissionIntent(context, permission);
    }

    /**
     * 是否有所有文件的管理权限
     */
    private static boolean isGrantedManageStoragePermission() {
        return Environment.isExternalStorageManager();
    }

    /**
     * 获取所有文件的管理权限设置界面意图
     */
    private static Intent getManageStoragePermissionIntent(Context context) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
        intent.setData(PermissionUtils.getPackageNameUri(context));

        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
        }

        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent = PermissionUtils.getApplicationDetailsIntent(context);
        }
        return intent;
    }
}