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

import com.yingda.rxtools.rxoaid.qiku.id.IOAIDInterface;
import com.yingda.rxtools.rxoaid.qiku.id.QikuIdmanager;

public class QikuImpl implements IOAID {
    private final Context context;
    private boolean mUseQikuId = true;

    public QikuImpl(Context context){
        this.context = context;
    }

    @Override
    public boolean supported() {
        if (context == null) {
            return false;
        }
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo("com.qiku.id", 0);
            if (pi != null){
                return true;
            }else{
                mUseQikuId = false;
                return new QikuIdmanager().isSupported();
            }
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
        if (mUseQikuId){
            Intent intent = new Intent("qiku.service.action.id");
            intent.setPackage("com.qiku.id");
            OAIDService.bind(context, intent, getter, new OAIDService.RemoteCaller() {
                @Override
                public String callRemoteInterface(IBinder service) throws OAIDException, RemoteException {
                    IOAIDInterface anInterface = IOAIDInterface.Stub.asInterface(service);
                    if (anInterface == null) {
                        throw new OAIDException("IdsSupplier is null");
                    }
                    return anInterface.getOAID();
                }
            });
        }else{
            try {
                String oaid = new QikuIdmanager().getOAID();
                if (oaid == null || oaid.length() == 0) {
                    throw new OAIDException("OAID/AAID acquire failed");
                }
                getter.onOAIDGetComplete(oaid);
            } catch (Exception e) {
                getter.onOAIDGetError(e);
            }
        }
    }
}
