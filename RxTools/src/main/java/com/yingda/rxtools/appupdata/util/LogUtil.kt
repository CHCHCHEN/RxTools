package com.yingda.rxtools.appupdate.util

import android.util.Log
import com.yingda.rxtools.appupdate.config.Constant


/**
 * author: chen
 * data: 2024/8/7
 * des: 
*/

class LogUtil {

    companion object {
        var b = true

        fun enable(enable: Boolean) {
            b = enable
        }

        fun e(tag: String, msg: String) {
            if (b) Log.e(Constant.TAG + tag, msg)
        }

        fun d(tag: String, msg: String) {
            if (b) Log.d(Constant.TAG + tag, msg)
        }

        fun i(tag: String, msg: String) {
            if (b) Log.i(Constant.TAG + tag, msg)
        }

    }
}