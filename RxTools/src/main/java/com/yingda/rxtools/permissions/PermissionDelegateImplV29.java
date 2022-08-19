package com.yingda.rxtools.permissions;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;

/**
 * author: chen
 * data: 2022/8/18
 * des: Android 10 权限委托实现
 */
@RequiresApi(api = Build.VERSION_CODES.Q)
class PermissionDelegateImplV29 extends PermissionDelegateImplV28 {

    @Override
    public boolean isGrantedPermission(Context context, String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.ACCESS_MEDIA_LOCATION)) {
            return hasReadStoragePermission(context) &&
                    PermissionUtils.checkSelfPermission(context, Permission.ACCESS_MEDIA_LOCATION);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.ACCESS_BACKGROUND_LOCATION) ||
                PermissionUtils.equalsPermission(permission, Permission.ACTIVITY_RECOGNITION)) {
            return PermissionUtils.checkSelfPermission(context, permission);
        }

        // 向下兼容 Android 11 新权限
        if (!AndroidVersion.Companion.isAndroid11()) {
            if (PermissionUtils.equalsPermission(permission, Permission.MANAGE_EXTERNAL_STORAGE)) {
                // 这个是 Android 10 上面的历史遗留问题，假设申请的是 MANAGE_EXTERNAL_STORAGE 权限
                // 必须要在 AndroidManifest.xml 中注册 android:requestLegacyExternalStorage="true"
                if (!isUseDeprecationExternalStorage()) {
                    return false;
                }
            }
        }

        return super.isGrantedPermission(context, permission);
    }

    @Override
    public boolean isPermissionPermanentDenied(Activity activity, String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.ACCESS_BACKGROUND_LOCATION)) {
            if (!PermissionUtils.checkSelfPermission(activity, Permission.ACCESS_FINE_LOCATION)) {
                return !PermissionUtils.shouldShowRequestPermissionRationale(activity, Permission.ACCESS_FINE_LOCATION);
            }
            return !PermissionUtils.checkSelfPermission(activity, permission) &&
                    !PermissionUtils.shouldShowRequestPermissionRationale(activity, permission);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.ACCESS_MEDIA_LOCATION)) {
            return hasReadStoragePermission(activity) &&
                    !PermissionUtils.checkSelfPermission(activity, permission) &&
                    !PermissionUtils.shouldShowRequestPermissionRationale(activity, permission);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.ACTIVITY_RECOGNITION)) {
            return !PermissionUtils.checkSelfPermission(activity, permission) &&
                    !PermissionUtils.shouldShowRequestPermissionRationale(activity, permission);
        }

        // 向下兼容 Android 11 新权限
        if (!AndroidVersion.Companion.isAndroid11()) {
            if (PermissionUtils.equalsPermission(permission, Permission.MANAGE_EXTERNAL_STORAGE)) {
                // 处理 Android 10 上面的历史遗留问题
                if (!isUseDeprecationExternalStorage()) {
                    return true;
                }
            }
        }

        return super.isPermissionPermanentDenied(activity, permission);
    }

    /**
     * 是否采用的是非分区存储的模式
     */
    private static boolean isUseDeprecationExternalStorage() {
        return Environment.isExternalStorageLegacy();
    }

    /**
     * 是否有读取文件的权限
     */
    private boolean hasReadStoragePermission(Context context) {
        if (AndroidVersion.Companion.isAndroid13() && AndroidVersion.Companion.getTargetSdkVersionCode(context) >= AndroidVersion.Companion.getANDROID_13()) {
            return PermissionUtils.checkSelfPermission(context, Permission.READ_MEDIA_IMAGES) ||
                    isGrantedPermission(context, Permission.MANAGE_EXTERNAL_STORAGE);
        }
        if (AndroidVersion.Companion.isAndroid11() && AndroidVersion.Companion.getTargetSdkVersionCode(context) >= AndroidVersion.Companion.getANDROID_11()) {
            return PermissionUtils.checkSelfPermission(context, Permission.READ_EXTERNAL_STORAGE) ||
                    isGrantedPermission(context, Permission.MANAGE_EXTERNAL_STORAGE);
        }
        return PermissionUtils.checkSelfPermission(context, Permission.READ_EXTERNAL_STORAGE);
    }
}