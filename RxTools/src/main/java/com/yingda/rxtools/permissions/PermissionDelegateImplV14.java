package com.yingda.rxtools.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.VpnService;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

/**
 * author: chen
 * data: 2023/5/11
 * des: ndroid 4.0 权限委托实现
 */
@RequiresApi(api = AndroidVersion.ANDROID_4_0)
class PermissionDelegateImplV14 implements PermissionDelegate {

    @Override
    public boolean isGrantedPermission(@NonNull Context context, @NonNull String permission) {
        // 检测 VPN 权限
        if (PermissionUtils.equalsPermission(permission, Permission.BIND_VPN_SERVICE)) {
            return isGrantedVpnPermission(context);
        }

        return true;
    }

    @Override
    public boolean isPermissionPermanentDenied(@NonNull Activity activity, @NonNull String permission) {
        return false;
    }

    @Override
    public Intent getPermissionIntent(@NonNull Context context, @NonNull String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.BIND_VPN_SERVICE)) {
            return getVpnPermissionIntent(context);
        }

        return PermissionUtils.getApplicationDetailsIntent(context);
    }

    /**
     * 是否有 VPN 权限
     */
    private static boolean isGrantedVpnPermission(@NonNull Context context) {
        return VpnService.prepare(context) == null;
    }

    /**
     * 获取 VPN 权限设置界面意图
     */
    private static Intent getVpnPermissionIntent(@NonNull Context context) {
        Intent intent = VpnService.prepare(context);
        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent = PermissionUtils.getApplicationDetailsIntent(context);
        }
        return intent;
    }
}