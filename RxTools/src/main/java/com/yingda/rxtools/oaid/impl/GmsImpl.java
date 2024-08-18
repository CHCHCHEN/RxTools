package com.yingda.rxtools.oaid.impl;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.IBinder;
import android.os.RemoteException;

import com.yingda.rxtools.oaid.IGetter;
import com.yingda.rxtools.oaid.IOAID;
import com.yingda.rxtools.oaid.OAIDException;
import com.yingda.rxtools.oaid.OAIDLog;

import com.yingda.rxtools.rxoaid.google.android.gms.ads.identifier.internal.IAdvertisingIdService;

/**
 * author: chen
 * data: 2024/8/18
 * des:
 */
class GmsImpl implements IOAID {
    private final Context context;

    public GmsImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean supported() {
        if (context == null) {
            return false;
        }
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo("com.android.vending", 0);
            return pi != null;
        } catch (Exception e) {
            OAIDLog.print(e);
            return false;
        }
    }

    @Override
    public void doGet(final IGetter getter) {
        if (context == null || getter == null) {
            return;
        }
        Intent intent = new Intent("com.google.android.gms.ads.identifier.service.START");
        intent.setPackage("com.google.android.gms");
        OAIDService.bind(context, intent, getter, new OAIDService.RemoteCaller() {
            @Override
            public String callRemoteInterface(IBinder service) throws OAIDException, RemoteException {
                IAdvertisingIdService anInterface = IAdvertisingIdService.Stub.asInterface(service);
                if (anInterface.isLimitAdTrackingEnabled(true)) {
                    // 实测在系统设置中停用了广告化功能也是能获取到广告标识符的
                    OAIDLog.print("User has disabled advertising identifier");
                }
                return anInterface.getId();
            }
        });
    }

}
