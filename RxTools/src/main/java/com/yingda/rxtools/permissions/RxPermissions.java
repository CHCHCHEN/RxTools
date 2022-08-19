package com.yingda.rxtools.permissions;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

/**
 * author: chen
 * data: 2022/8/18
 * des: Android 危险权限请求类
*/
@SuppressWarnings({"unused"})
public final class RxPermissions {

    /** 权限设置页跳转请求码 */
    public static final int REQUEST_CODE = 1024 + 1;

    /** 权限请求拦截器 */
    private static IPermissionInterceptor sInterceptor;

    /** 当前是否为检查模式 */
    private static Boolean sCheckMode;

    /**
     * 设置请求的对象
     *
     * @param context          当前 Activity，可以传入栈顶的 Activity
     */
    public static RxPermissions with(Context context) {
        return new RxPermissions(context);
    }

    public static RxPermissions with(Fragment fragment) {
        return with(fragment.getActivity());
    }

    public static RxPermissions with(androidx.fragment.app.Fragment fragment) {
        return with(fragment.getActivity());
    }

    /**
     * 是否为检查模式
     */
    public static void setCheckMode(boolean checkMode) {
        sCheckMode = checkMode;
    }

    /**
     * 设置全局权限请求拦截器
     */
    public static void setInterceptor(IPermissionInterceptor interceptor) {
        sInterceptor = interceptor;
    }

    /**
     * 获取全局权限请求拦截器
     */
    public static IPermissionInterceptor getInterceptor() {
        if (sInterceptor == null) {
            sInterceptor = new IPermissionInterceptor() {};
        }
        return sInterceptor;
    }

    /** Context 对象 */
    private final Context mContext;

    /** 权限列表 */
    private List<String> mPermissions;

    /** 权限请求拦截器 */
    private IPermissionInterceptor mInterceptor;

    /** 设置不检查 */
    private Boolean mCheckMode;

    /**
     * 私有化构造函数
     */
    private RxPermissions(Context context) {
        mContext = context;
    }

    /**
     * 添加权限组
     */
    public RxPermissions permission(String... permissions) {
        return permission(PermissionUtils.asArrayList(permissions));
    }

    public RxPermissions permission(String[]... permissions) {
        return permission(PermissionUtils.asArrayLists(permissions));
    }

    public RxPermissions permission(List<String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return this;
        }
        if (mPermissions == null) {
            mPermissions = new ArrayList<>(permissions);
            return this;
        }

        for (String permission : permissions) {
            if (PermissionUtils.containsPermission(mPermissions, permission)) {
                continue;
            }
            mPermissions.add(permission);
        }
        return this;
    }

    /**
     * 设置权限请求拦截器
     */
    public RxPermissions interceptor(IPermissionInterceptor interceptor) {
        mInterceptor = interceptor;
        return this;
    }

    /**
     * 设置不触发错误检测机制
     */
    public RxPermissions unchecked() {
        mCheckMode = false;
        return this;
    }

    /**
     * 请求权限
     */
    public void request(OnPermissionCallback callback) {
        if (mContext == null) {
            return;
        }

        if (mInterceptor == null) {
            mInterceptor = getInterceptor();
        }

        // 权限请求列表（为什么直接不用字段？因为框架要兼容新旧权限，在低版本下会自动添加旧权限申请）
        List<String> permissions = new ArrayList<>(mPermissions);

        boolean checkMode = isCheckMode();

        // 检查当前 Activity 状态是否是正常的，如果不是则不请求权限
        Activity activity = PermissionUtils.findActivity(mContext);
        if (!PermissionChecker.checkActivityStatus(activity, checkMode)) {
            return;
        }

        // 必须要传入正常的权限或者权限组才能申请权限
        if (!PermissionChecker.checkPermissionArgument(permissions, checkMode)) {
            return;
        }

        if (checkMode) {
            // 检查申请的读取媒体位置权限是否符合规范
            PermissionChecker.checkMediaLocationPermission(mContext, permissions);
            // 检查申请的存储权限是否符合规范
            PermissionChecker.checkStoragePermission(mContext, permissions);
            // 检查申请的传感器权限是否符合规范
            PermissionChecker.checkBodySensorsPermission(permissions);
            // 检查申请的定位权限是否符合规范
            PermissionChecker.checkLocationPermission(mContext, permissions);
            // 检查申请的权限和 targetSdk 版本是否能吻合
            PermissionChecker.checkTargetSdkVersion(mContext, permissions);
            // 检测权限有没有在清单文件中注册
            PermissionChecker.checkManifestPermissions(mContext, permissions);
        }

        // 优化所申请的权限列表
        PermissionChecker.optimizeDeprecatedPermission(permissions);

        if (PermissionApi.isGrantedPermissions(mContext, permissions)) {
            // 证明这些权限已经全部授予过，直接回调成功
            if (callback != null) {
                mInterceptor.grantedPermissions(activity, permissions, permissions, true, callback);
            }
            return;
        }

        // 申请没有授予过的权限
        mInterceptor.requestPermissions(activity, permissions, callback);
    }

    /**
     * 撤销权限并杀死当前进程
     *
     * @return          返回 true 代表成功，返回 false 代表失败
     */
    public boolean revokeOnKill() {
        if (mContext == null) {
            return false;
        }

        if (!AndroidVersion.Companion.isAndroid13()) {
            return false;
        }

        try {
            if (mPermissions.size() == 1) {
                // API 文档：https://developer.android.google.cn/reference/android/content/Context#revokeSelfPermissionOnKill(java.lang.String)
                mContext.revokeSelfPermissionOnKill(mPermissions.get(0));
            } else {
                // API 文档：https://developer.android.google.cn/reference/android/content/Context#revokeSelfPermissionsOnKill(java.util.Collection%3Cjava.lang.String%3E)
                mContext.revokeSelfPermissionsOnKill(mPermissions);
            }
            return true;
        } catch (IllegalArgumentException e) {
            if (isCheckMode()) {
                throw e;
            }
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 当前是否为检测模式
     */
    private boolean isCheckMode() {
        if (mCheckMode == null) {
            if (sCheckMode == null) {
                sCheckMode = PermissionUtils.isDebugMode(mContext);
            }
            mCheckMode = sCheckMode;
        }
        return mCheckMode;
    }

    /**
     * 判断一个或多个权限是否全部授予了
     */
    public static boolean isGranted(Context context, String... permissions) {
        return isGranted(context, PermissionUtils.asArrayList(permissions));
    }

    public static boolean isGranted(Context context, String[]... permissions) {
        return isGranted(context, PermissionUtils.asArrayLists(permissions));
    }

    public static boolean isGranted(Context context, List<String> permissions) {
        return PermissionApi.isGrantedPermissions(context, permissions);
    }

    /**
     * 获取没有授予的权限
     */
    public static List<String> getDenied(Context context, String... permissions) {
        return getDenied(context, PermissionUtils.asArrayList(permissions));
    }

    public static List<String> getDenied(Context context, String[]... permissions) {
        return getDenied(context, PermissionUtils.asArrayLists(permissions));
    }

    public static List<String> getDenied(Context context, List<String> permissions) {
        return PermissionApi.getDeniedPermissions(context, permissions);
    }

    /**
     * 判断某个权限是否为特殊权限
     */
    public static boolean isSpecial(String permission) {
        return PermissionApi.isSpecialPermission(permission);
    }

    /**
     * 判断权限列表中是否包含特殊权限
     */
    public static boolean containsSpecial(String... permissions) {
        return containsSpecial(PermissionUtils.asArrayList(permissions));
    }

    public static boolean containsSpecial(List<String> permissions) {
        return PermissionApi.containsSpecialPermission(permissions);
    }

    /**
     * 判断一个或多个权限是否被永久拒绝了
     *
     * （注意不能在请求权限之前调用，应该在 {@link OnPermissionCallback#onDenied(List, boolean)} 方法中调用）
     */
    public static boolean isPermanentDenied(Activity activity, String... permissions) {
        return isPermanentDenied(activity, PermissionUtils.asArrayList(permissions));
    }

    public static boolean isPermanentDenied(Activity activity, String[]... permissions) {
        return isPermanentDenied(activity, PermissionUtils.asArrayLists(permissions));
    }

    public static boolean isPermanentDenied(Activity activity, List<String> permissions) {
        return PermissionApi.isPermissionPermanentDenied(activity, permissions);
    }

    /* android.content.Context */

    public static void startPermissionActivity(Context context) {
        startPermissionActivity(context, (List<String>) null);
    }

    public static void startPermissionActivity(Context context, String... permissions) {
        startPermissionActivity(context, PermissionUtils.asArrayList(permissions));
    }

    public static void startPermissionActivity(Context context, String[]... permissions) {
        startPermissionActivity(context, PermissionUtils.asArrayLists(permissions));
    }

    /**
     * 跳转到应用权限设置页
     *
     * @param permissions           没有授予或者被拒绝的权限组
     */
    public static void startPermissionActivity(Context context, List<String> permissions) {
        Activity activity = PermissionUtils.findActivity(context);
        if (activity != null) {
            startPermissionActivity(activity, permissions);
            return;
        }
        Intent intent = PermissionUtils.getSmartPermissionIntent(context, permissions);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    /* android.app.Activity */

    public static void startPermissionActivity(Activity activity) {
        startPermissionActivity(activity, (List<String>) null);
    }

    public static void startPermissionActivity(Activity activity, String... permissions) {
        startPermissionActivity(activity, PermissionUtils.asArrayList(permissions));
    }

    public static void startPermissionActivity(Activity activity, String[]... permissions) {
        startPermissionActivity(activity, PermissionUtils.asArrayLists(permissions));
    }

    public static void startPermissionActivity(Activity activity, List<String> permissions) {
        startPermissionActivity(activity, permissions, REQUEST_CODE);
    }

    public static void startPermissionActivity(Activity activity, List<String> permissions, int requestCode) {
        activity.startActivityForResult(PermissionUtils.getSmartPermissionIntent(activity, permissions), requestCode);
    }

    public static void startPermissionActivity(Activity activity, String permission, OnPermissionPageCallback callback) {
        startPermissionActivity(activity, PermissionUtils.asArrayList(permission), callback);
    }

    public static void startPermissionActivity(Activity activity, String[] permissions, OnPermissionPageCallback callback) {
        startPermissionActivity(activity, PermissionUtils.asArrayLists(permissions), callback);
    }

    public static void startPermissionActivity(Activity activity, List<String> permissions, OnPermissionPageCallback callback) {
        PermissionPageFragment.beginRequest(activity, (ArrayList<String>) permissions, callback);
    }

    /* android.app.Fragment */

    public static void startPermissionActivity(Fragment fragment) {
        startPermissionActivity(fragment, (List<String>) null);
    }

    public static void startPermissionActivity(Fragment fragment, String... permissions) {
        startPermissionActivity(fragment, PermissionUtils.asArrayList(permissions));
    }

    public static void startPermissionActivity(Fragment fragment, String[]... permissions) {
        startPermissionActivity(fragment, PermissionUtils.asArrayLists(permissions));
    }

    public static void startPermissionActivity(Fragment fragment, List<String> permissions) {
        startPermissionActivity(fragment, permissions, REQUEST_CODE);
    }

    public static void startPermissionActivity(Fragment fragment, List<String> permissions, int requestCode) {
        Activity activity = fragment.getActivity();
        if (activity == null) {
            return;
        }
        fragment.startActivityForResult(PermissionUtils.getSmartPermissionIntent(activity, permissions), requestCode);
    }

    public static void startPermissionActivity(Fragment fragment, String permission, OnPermissionPageCallback callback) {
        startPermissionActivity(fragment, PermissionUtils.asArrayList(permission), callback);
    }

    public static void startPermissionActivity(Fragment fragment, String[] permissions, OnPermissionPageCallback callback) {
        startPermissionActivity(fragment, PermissionUtils.asArrayLists(permissions), callback);
    }

    public static void startPermissionActivity(Fragment fragment, List<String> permissions, OnPermissionPageCallback callback) {
        Activity activity = fragment.getActivity();
        if (activity == null || activity.isFinishing()) {
            return;
        }
        if (Build.VERSION.SDK_INT >= AndroidVersion.Companion.getANDROID_4_2() && activity.isDestroyed()) {
            return;
        }
        PermissionPageFragment.beginRequest(activity, (ArrayList<String>) permissions, callback);
    }

    /* androidx.fragment.app.Fragment */

    public static void startPermissionActivity(androidx.fragment.app.Fragment fragment) {
        startPermissionActivity(fragment, (List<String>) null);
    }

    public static void startPermissionActivity(androidx.fragment.app.Fragment fragment, String... permissions) {
        startPermissionActivity(fragment, PermissionUtils.asArrayList(permissions));
    }

    public static void startPermissionActivity(androidx.fragment.app.Fragment fragment, String[]... permissions) {
        startPermissionActivity(fragment, PermissionUtils.asArrayLists(permissions));
    }

    public static void startPermissionActivity(androidx.fragment.app.Fragment fragment, List<String> permissions) {
        startPermissionActivity(fragment, permissions, REQUEST_CODE);
    }

    public static void startPermissionActivity(androidx.fragment.app.Fragment fragment, List<String> permissions, int requestCode) {
        Activity activity = fragment.getActivity();
        if (activity == null) {
            return;
        }
        fragment.startActivityForResult(PermissionUtils.getSmartPermissionIntent(activity, permissions), requestCode);
    }

    public static void startPermissionActivity(androidx.fragment.app.Fragment fragment, String permission, OnPermissionPageCallback callback) {
        startPermissionActivity(fragment, PermissionUtils.asArrayList(permission), callback);
    }

    public static void startPermissionActivity(androidx.fragment.app.Fragment fragment, String[] permissions, OnPermissionPageCallback callback) {
        startPermissionActivity(fragment, PermissionUtils.asArrayLists(permissions), callback);
    }

    public static void startPermissionActivity(androidx.fragment.app.Fragment fragment, List<String> permissions, OnPermissionPageCallback callback) {
        Activity activity = fragment.getActivity();
        if (activity == null || activity.isFinishing()) {
            return;
        }
        if (Build.VERSION.SDK_INT >= AndroidVersion.Companion.getANDROID_4_2() && activity.isDestroyed()) {
            return;
        }
        PermissionPageFragment.beginRequest(activity, (ArrayList<String>) permissions, callback);
    }
}