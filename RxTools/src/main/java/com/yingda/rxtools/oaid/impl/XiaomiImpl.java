package com.yingda.rxtools.oaid.impl;

import android.annotation.SuppressLint;
import android.content.Context;

import com.yingda.rxtools.oaid.IGetter;
import com.yingda.rxtools.oaid.IOAID;
import com.yingda.rxtools.oaid.OAIDException;
import com.yingda.rxtools.oaid.OAIDLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * author: chen
 * data: 2024/8/18
 * des:  * 参阅 <a href="http://f4.market.xiaomi.com/download/MiPass/058fc4374ac89aea6dedd9dc03c60a5498241e0dd/DeviceId.jar">DeviceId.jar</a>
 *  * 即 com.miui.deviceid.IdentifierManager
*/
class XiaomiImpl implements IOAID {
    private final Context context;
    private Class<?> idProviderClass;
    private Object idProviderImpl;

    @SuppressLint("PrivateApi")
    public XiaomiImpl(Context context) {
        this.context = context;
        try {
            idProviderClass = Class.forName("com.android.id.impl.IdProviderImpl");
            idProviderImpl = idProviderClass.newInstance();
        } catch (Exception e) {
            OAIDLog.print(e);
        }
    }

    @Override
    public boolean supported() {
        return idProviderImpl != null;
    }

    @Override
    public void doGet(final IGetter getter) {
        if (context == null || getter == null) {
            return;
        }
        if (idProviderClass == null || idProviderImpl == null) {
            getter.onOAIDGetError(new OAIDException("Xiaomi IdProvider not exists"));
            return;
        }
        try {
            String oaid = getOAID();
            if (oaid == null || oaid.length() == 0) {
                throw new OAIDException("OAID query failed");
            }
            OAIDLog.print("OAID query success: " + oaid);
            getter.onOAIDGetComplete(oaid);
        } catch (Exception e) {
            OAIDLog.print(e);
            getter.onOAIDGetError(e);
        }
    }

    private String getOAID() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = idProviderClass.getMethod("getOAID", Context.class);
        return (String) method.invoke(idProviderImpl, context);
    }

}
