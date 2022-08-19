package com.yingda.rxtools.permissions;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * author: chen
 * data: 2022/8/18
 * des: Android 9.0 权限委托实现
*/
@RequiresApi(api = Build.VERSION_CODES.P)
class PermissionDelegateImplV28 extends PermissionDelegateImplV26 {

   @Override
   public boolean isGrantedPermission(Context context, String permission) {
      if (PermissionUtils.equalsPermission(permission, Permission.ACCEPT_HANDOVER)) {
         return PermissionUtils.checkSelfPermission(context, permission);
      }
      return super.isGrantedPermission(context, permission);
   }

   @Override
   public boolean isPermissionPermanentDenied(Activity activity, String permission) {
      if (PermissionUtils.equalsPermission(permission, Permission.ACCEPT_HANDOVER)) {
         return !PermissionUtils.checkSelfPermission(activity, permission) &&
                 !PermissionUtils.shouldShowRequestPermissionRationale(activity, permission);
      }
      return super.isPermissionPermanentDenied(activity, permission);
   }
}