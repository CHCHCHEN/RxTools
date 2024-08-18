package com.yingda.rxtools.oaid.impl;

import android.content.Context;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;

import com.yingda.rxtools.oaid.IGetter;
import com.yingda.rxtools.oaid.IOAID;
import com.yingda.rxtools.oaid.OAIDException;
import com.yingda.rxtools.oaid.OAIDLog;

import java.util.Objects;

/**
 * author: chen
 * data: 2024/8/18
 * des:
*/
class MeizuImpl implements IOAID {
    private final Context context;

    public MeizuImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean supported() {
        if (context == null) {
            return false;
        }
        try {
            ProviderInfo pi = context.getPackageManager().resolveContentProvider("com.meizu.flyme.openidsdk", 0);
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
        Uri uri = Uri.parse("content://com.meizu.flyme.openidsdk/");
        try (Cursor cursor = context.getContentResolver().query(uri, null, null,
                new String[]{"oaid"}, null)) {
            Objects.requireNonNull(cursor).moveToFirst();
            String oaid = cursor.getString(cursor.getColumnIndex("value"));
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

}
