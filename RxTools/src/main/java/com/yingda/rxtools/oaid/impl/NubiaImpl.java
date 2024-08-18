package com.yingda.rxtools.oaid.impl;

import android.annotation.SuppressLint;
import android.content.ContentProviderClient;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.yingda.rxtools.oaid.IGetter;
import com.yingda.rxtools.oaid.IOAID;
import com.yingda.rxtools.oaid.OAIDException;
import com.yingda.rxtools.oaid.OAIDLog;

/**
 * author: chen
 * data: 2024/8/18
 * des:
*/
class NubiaImpl implements IOAID {
    private final Context context;

    public NubiaImpl(Context context) {
        this.context = context;
    }

    @SuppressLint("AnnotateVersionCheck")
    @Override
    public boolean supported() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }

    @Override
    public void doGet(final IGetter getter) {
        if (context == null || getter == null) {
            return;
        }
        if (!supported()) {
            String message = "Only supports Android 10.0 and above for Nubia";
            OAIDLog.print(message);
            getter.onOAIDGetError(new OAIDException(message));
            return;
        }
        String oaid = null;
        try {
            Uri uri = Uri.parse("content://cn.nubia.identity/identity");
            ContentProviderClient client = context.getContentResolver().acquireContentProviderClient(uri);
            if (client == null) {
                return;
            }
            Bundle bundle = client.call("getOAID", null, null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                client.close();
            } else {
                client.release();
            }
            if (bundle == null) {
                throw new OAIDException("OAID query failed: bundle is null");
            }
            if (bundle.getInt("code", -1) == 0) {
                oaid = bundle.getString("id");
            }
            if (oaid == null || oaid.length() == 0) {
                throw new OAIDException("OAID query failed: " + bundle.getString("message"));
            }
            OAIDLog.print("OAID query success: " + oaid);
            getter.onOAIDGetComplete(oaid);
        } catch (Exception e) {
            OAIDLog.print(e);
            getter.onOAIDGetError(e);
        }
    }

}
