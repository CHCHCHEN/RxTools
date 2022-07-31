package com.yingda.rxtools.log.inner;

import android.util.Log;

/**
 * author: chen
 * data: 2022/7/31
 * des: Logcat树-输出日志信息到Logcat
*/
public class LogcatTree extends Tree {
    @Override
    protected void log(int type, String tag, String message) {
        switch (type) {
            case Log.VERBOSE:
                Log.v(tag, message);
                break;
            case Log.INFO:
                Log.i(tag, message);
                break;
            case Log.DEBUG:
                Log.d(tag, message);
                break;
            case Log.WARN:
                Log.w(tag, message);
                break;
            case Log.ERROR:
                Log.e(tag, message);
                break;
            case Log.ASSERT:
                Log.wtf(tag, message);
                break;
            default:
                break;
        }
    }
}
