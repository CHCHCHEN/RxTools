package com.yingda.rxtools.oaid.impl;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.IBinder;
import android.os.RemoteException;

import com.yingda.rxtools.oaid.IGetter;
import com.yingda.rxtools.oaid.IOAID;
import com.yingda.rxtools.oaid.OAIDException;
import com.yingda.rxtools.oaid.OAIDLog;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.yingda.rxtools.rxoaid.heytap.openid.IOpenID;
/**
 * author: chen
 * data: 2024/8/18
 * des:
*/
class OppoImpl implements IOAID {
    private final Context context;
    private String sign;

    public OppoImpl(Context context) {
        if (context instanceof Application) {
            this.context = context;
        } else {
            this.context = context.getApplicationContext();
        }
    }

    @Override
    public boolean supported() {
        if (context == null) {
            return false;
        }
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo("com.heytap.openid", 0);
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
        Intent intent = new Intent("action.com.heytap.openid.OPEN_ID_SERVICE");
        intent.setComponent(new ComponentName("com.heytap.openid", "com.heytap.openid.IdentifyService"));
        OAIDService.bind(context, intent, getter, new OAIDService.RemoteCaller() {
            @Override
            public String callRemoteInterface(IBinder service) throws OAIDException, RemoteException {
                try {
                    return realGetOUID(service);
                } catch (OAIDException | RemoteException e) {
                    throw e;
                } catch (Exception e) {
                    throw new OAIDException(e);
                }
            }
        });
    }

    @SuppressLint("PackageManagerGetSignatures")
    protected String realGetOUID(IBinder service) throws PackageManager.NameNotFoundException,
            NoSuchAlgorithmException, RemoteException, OAIDException {
        String pkgName = context.getPackageName();
        if (sign == null) {
            Signature[] signatures = context.getPackageManager().getPackageInfo(pkgName,
                    PackageManager.GET_SIGNATURES).signatures;
            byte[] byteArray = signatures[0].toByteArray();
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            byte[] digest = messageDigest.digest(byteArray);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(Integer.toHexString((b & 255) | 256).substring(1, 3));
            }
            sign = sb.toString();
            return getSerId(service, pkgName, sign);
        }
        return getSerId(service, pkgName, sign);
    }

    protected String getSerId(IBinder service, String pkgName, String sign) throws RemoteException, OAIDException {
        IOpenID anInterface = IOpenID.Stub.asInterface(service);
        if (anInterface == null) {
            throw new OAIDException("IOpenID is null");
        }
        return anInterface.getSerID(pkgName, sign, "OUID");
    }

}
