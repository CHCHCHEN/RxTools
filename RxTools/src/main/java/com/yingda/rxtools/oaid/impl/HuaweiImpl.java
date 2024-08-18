package com.yingda.rxtools.oaid.impl;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;

import com.yingda.rxtools.oaid.IGetter;
import com.yingda.rxtools.oaid.IOAID;
import com.yingda.rxtools.oaid.OAIDException;
import com.yingda.rxtools.oaid.OAIDLog;
import com.huawei.hms.ads.identifier.AdvertisingIdClient;

import java.io.IOException;
import java.util.concurrent.Executors;

/**
 * author: chen
 * data: 2024/8/18
 * des: 参阅华为官方 <a href="https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/identifier-service-integrating-sdk-0000001056460552">HUAWEI Ads SDK</a>。
 *  *
*/
class HuaweiImpl implements IOAID {
    private final Context context;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    public HuaweiImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean supported() {
        if (context == null) {
            return false;
        }
        try {
            if (AdvertisingIdClient.isAdvertisingIdAvailable(context)) {
                return true;
            }
            PackageManager pm = context.getPackageManager();
            if (pm.getPackageInfo("com.huawei.hwid", 0) != null) {
                return true;
            }
            if (pm.getPackageInfo("com.huawei.hwid.tv", 0) != null) {
                return true;
            }
            if (pm.getPackageInfo("com.huawei.hms", 0) != null) {
                return true;
            }
        } catch (Exception e) {
            OAIDLog.print(e);
        }
        return false;
    }

    @Override
    public void doGet(final IGetter getter) {
        if (context == null || getter == null) {
            return;
        }
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                runOnSubThread(getter);
            }
        });
    }

    private void runOnSubThread(IGetter getter) {
        try {
            // 获取OAID信息（SDK方式）
            // 参阅 https://developer.huawei.com/consumer/cn/doc/HMSCore-Guides/identifier-service-obtaining-oaid-sdk-0000001050064988
            // 华为官方开发者文档提到“调用getAdvertisingIdInfo接口，获取OAID信息，不要在主线程中调用该方法。”
            final AdvertisingIdClient.Info info = AdvertisingIdClient.getAdvertisingIdInfo(context);
            if (info == null) {
                postOnMainThread(getter, new OAIDException("Advertising identifier info is null"));
                return;
            }
            if (info.isLimitAdTrackingEnabled()) {
                // 实测在系统设置中关闭了广告标识符，将获取到固定的一大堆0
                postOnMainThread(getter, new OAIDException("User has disabled advertising identifier"));
                return;
            }
            postOnMainThread(getter, info.getId());
        } catch (IOException e) {
            OAIDLog.print(e);
            postOnMainThread(getter, new OAIDException(e));
        }
    }

    private void postOnMainThread(final IGetter getter, final String oaid) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                getter.onOAIDGetComplete(oaid);
            }
        });
    }

    private void postOnMainThread(final IGetter getter, final OAIDException e) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                getter.onOAIDGetError(e);
            }
        });
    }

}
