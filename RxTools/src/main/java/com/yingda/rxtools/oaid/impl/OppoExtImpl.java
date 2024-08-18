package com.yingda.rxtools.oaid.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.IBinder;
import android.os.RemoteException;

import com.yingda.rxtools.oaid.IGetter;
import com.yingda.rxtools.oaid.OAIDException;
import com.yingda.rxtools.oaid.OAIDLog;

import com.yingda.rxtools.rxoaid.oplus.stdid.IStdID;

/**
 * author: chen
 * data: 2024/8/18
 * des:
*/
public class OppoExtImpl extends OppoImpl{
    private final static String ACTION = "action.com.oplus.stdid.ID_SERVICE";
    private final static String PACKAGE_NAME = "com.coloros.mcs";
    private final static String CLASS_NAME = "com.oplus.stdid.IdentifyService";

    private final Context context;
    public OppoExtImpl(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public boolean supported() {
        if (context == null) {
            return false;
        }
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(PACKAGE_NAME, 0);
            return pi != null;
        } catch (Exception e) {
            OAIDLog.print(e);
            return false;
        }
    }

    @Override
    public void doGet(IGetter getter) {
        if (context == null || getter == null) {
            return;
        }
        Intent intent = new Intent(ACTION);
        intent.setComponent(new ComponentName(PACKAGE_NAME, CLASS_NAME));
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

    protected String getSerId(IBinder service, String pkgName, String sign) throws RemoteException, OAIDException {
        IStdID anInterface = IStdID.Stub.asInterface(service);
        if (anInterface == null) {
            throw new OAIDException("IStdID is null");
        }
        return anInterface.getSerID(pkgName, sign, "OUID");
    }
}
